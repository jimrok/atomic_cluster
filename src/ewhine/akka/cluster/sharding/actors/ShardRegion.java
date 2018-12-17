package ewhine.akka.cluster.sharding.actors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import ewhine.actor.message.GetShardHome;
import ewhine.actor.message.ShardInitialized;
import ewhine.actor.message.StartEntity;
import ewhine.actor.message.Terminated;
import ewhine.akka.cluster.sharding.MessageExtractor;

public class ShardRegion extends AbstractActor {
	final static private Logger LOG = LoggerFactory.getLogger(ShardRegion.class
			.getName());

	private Set<ActorRef> coordinator = new HashSet<ActorRef>();
	private HashMap<ActorRef, List<ActorRef>> regions = new HashMap<ActorRef, List<ActorRef>>();
	private HashMap<ActorRef, String> shardsByRef = new HashMap<ActorRef, String>();
	private HashMap<String, ActorRef> shards = new HashMap<String, ActorRef>();
	private Set<ActorRef> handingOff = new HashSet<ActorRef>();
	private HashMap<String, ActorRef> regionByShard = new HashMap<>(); // shard
																		// Id获得ShardRegion的ref.
	private Set<String> startingShards = new HashSet<String>();
	private ShardBuffer shardBuffers = new ShardBuffer();

	private MessageExtractor message_extractor = null;

	private boolean loggedFullBufferWarning;
	private int retryCount;

	@Override
	public void preStart() throws Exception {
		if (LOG.isInfoEnabled())
			LOG.info("DeadLetterActor preStart() called.");
		super.preStart();
	}

	@Override
	public void postStop() throws Exception {
		if (LOG.isInfoEnabled())
			LOG.info("DeadLetterActor postStop() called.");
		super.postStop();
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.match(ShardInitialized.class,
						msg -> initializeShard(msg.getShardId(), getSender()))
				.match(Terminated.class, ref -> receiveTerminated(ref.getRef()))
				.match(Object.class,
						message -> {
							if (message instanceof MessageExtractor) {
								deliverMessage(message, getSender());
							} else {
								if (LOG.isWarnEnabled())
									LOG.warn(
											"Message does not have an extractor defined in message, so it was ignored: {}",
											message);
							}
						}).build();
	}

	private void initializeShard(String shardId, ActorRef shard) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Shard was initialized {}", shardId);
		}
		startingShards.remove(shardId);
		deliverBufferedMessages(shardId, shard);
	}

	private void deliverStartEntity(StartEntity msg, ActorRef snd) {

	}

	private void deliverMessage(Object msg, ActorRef snd) {
		if (msg instanceof RestartShard) {
			// 创建和启动Shard
			RestartShard restart_shard = (RestartShard) msg;
			String shard_id = restart_shard.getShardId();
			ActorRef region_ref = regionByShard.get(shard_id);
			if (region_ref == null) {
				
				if (!shardBuffers.contains(shard_id)) {
					// 需要刷新一下coordinator,等待shard的actor启动就绪
					LOG.debug("Request shard [{}] home. Coordinator [{}]",shard_id,coordinator);
					coordinator.forEach(x -> {
						x.tell(new GetShardHome(shard_id), getSelf());
					});
				}
				List<BufferdMessage> buf = shardBuffers.getOrEmpty(shard_id);
				LOG.debug("Buffer message for shard [{}]. Total [{}] buffered messages.", shard_id, buf.size() + 1);
	            shardBuffers.append(shard_id, msg, snd);

				

			} else if (region_ref.equals(getSelf())) {
				// 如果启动的shard就是当前的region管理的
				getShard(shard_id);
				
			}
			return;
		}

		String shard_id = extractShardId(msg);
		
		if (shard_id == null || shard_id.length() == 0) {
			if (LOG.isWarnEnabled()) {
				LOG.warn("Shard must not be empty, dropping message [{}]", msg
						.getClass().getName());
			}
			// 不是一个正常的消息，扔给死信队列
			getContext().system().deadLetters().tell(msg, getSelf());
			return;
		}

		ActorRef region_ref = regionByShard.get(shard_id);
		if (region_ref == null) {

			if (!shardBuffers.contains(shard_id)) {
				// 需要刷新一下coordinator,等待shard的actor启动就绪
				LOG.debug("Request shard [{}] home. Coordinator [{}]",shard_id,coordinator);
				coordinator.forEach(x -> {
					x.tell(new GetShardHome(shard_id), getSelf());
				});
			}

			bufferMessage(shard_id, msg, snd);
		}

		if (region_ref != null) {
			if (region_ref.equals(getSelf())) {
				// 本地的消息
				ActorRef shard_ref = getShard(shard_id);
				if (shard_ref != null) { //Some(share_id)
					if (shardBuffers.contains(shard_id)) {
						//现在发往一个Shard的消息是顺序缓存的，现在开始发送了。
						bufferMessage(shard_id, msg, snd);
						deliverBufferedMessages(shard_id, shard_ref);
					} else {
						// 本地的shard，直接发消息
						shard_ref.tell(msg, snd);
					}
				} else {
					// 不存在share，那先缓存了。？那什么时候发？shard可能需要重新分配到一个合适的Region
					bufferMessage(shard_id, msg, snd);
				}
			} else {
				// 远程的region,把消息发送给这个远程的region ？这个合适吗？
				LOG.debug("Forwarding request for shard [{}] to [{}]", shard_id, region_ref);
				region_ref.tell(msg, snd);

			}
		}

	}

	
	private void deliverBufferedMessages(String shard_id, ActorRef receiver) {
		// TODO 把shardBuffer中的消息发送出去，目前的buffer的实现是Message留在内存中，存在溢出的问题。

		if (shardBuffers.contains(shard_id)) {
			List<BufferdMessage> buf = shardBuffers.getOrEmpty(shard_id);
			if (LOG.isDebugEnabled()) {
				LOG.debug("Deliver [{}] buffered messages for shard [{}]",
						buf.size(), shard_id);
			}
			buf.forEach(x -> {
				receiver.tell(x.getMessage(), x.getSender());
			});
			shardBuffers.remove(shard_id);
		}

		loggedFullBufferWarning = false;
		retryCount = 0;
	}

	private void bufferMessage(String shard_id, Object msg, ActorRef snd) {
		// TODO: 研究一下，看看要不要限制最大的缓存消息数量。
		shardBuffers.append(shard_id, msg, snd);
	}

	private String extractShardId(Object msg) {
		return message_extractor.shardId(msg);
	}

	private ActorRef getShard(String shard_id) {
		// TODO 查找并创建这个shard的actor

		if (startingShards.contains(shard_id)) {
			return null;
		} else {
			if (!shardsByRef.values().contains(shard_id)) {
				ActorContext context = getContext();
				ActorRef shard = context.actorOf(Shard.props());
				shardsByRef.put(shard, shard_id);
				shards.put(shard_id, shard);
				startingShards.add(shard_id);
			} else {
				LOG.error("IllegalStateException found in shard_id:" + shard_id);
				throw new IllegalStateException(
						"Shard must not be allocated to a proxy only ShardRegion");
			}
		}

		return null;
	}

	private void receiveTerminated(ActorRef ref) {
		if (coordinator.contains(ref)) {
			coordinator = null;
		} else if (regions.containsKey(ref)) {
			List<ActorRef> shards = regions.get(ref);
			// TODO: rebuild code.
			shards.remove(ref);
			if (LOG.isDebugEnabled()) {
				LOG.debug("Region [{}] with shards [{}] terminated", ref,
						shards);
			}

		} else if (shardsByRef.containsKey(ref)) {
			String shardId = shardsByRef.get(ref);
			shardsByRef.remove(ref);
			if (handingOff.contains(ref)) {
				handingOff.remove(ref);
				if (LOG.isDebugEnabled()) {
					LOG.debug("Shard [{}] handoff complete", shardId);
				}
			} else {

			}
			tryCompleteGracefulShutdown();
		}

	}

	private void tryCompleteGracefulShutdown() {
		// TODO Auto-generated method stub

	}

}
