package ewhine.akka.cluster.sharding.actors;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.ReceiveTimeout;
import ewhine.actor.message.ShardInitialized;
import ewhine.actor.message.StartEntity;
import ewhine.actor.message.StartEntityAck;
import ewhine.akka.cluster.sharding.MessageExtractor;

public class ShardActor extends AbstractActor {
	final static private Logger LOG = LoggerFactory.getLogger(ShardActor.class
			.getName());

	private String shard_id;
	private MessageExtractor messageExtractor;
	private Map<ActorRef, String> idByRef = new HashMap<>();
	private Map<String, ActorRef> refById = new HashMap<>();
	private ShardEntityMessagesBuffer messageBuffers = new ShardEntityMessagesBuffer();

	private Class<AbstractActor> entityClazz;

	private int bufferSize = 1000;

	public ShardActor(String shardId, MessageExtractor messageExtractor,
			Class<AbstractActor> entityClass) {
		this.shard_id = shardId;
		this.messageExtractor = messageExtractor;
		this.entityClazz = entityClass;
	}

	// public UserActor(long user_id) {
	// this.id = user_id;
	// }

	@Override
	public Receive createReceive() {
		return receiveBuilder().match(MessageExtractor.class, message -> {

			deliverMessage(message, getSender());
		}).matchEquals(ReceiveTimeout.getInstance(), msg -> passivate())
				.build();
	}

	@Override
	public void preStart() throws Exception {
		super.preStart();
		LOG.info("shard actor:" + this.shard_id + " started!");
		getContext().parent().tell(new ShardInitialized(this.shard_id),
				getSelf());
	}

	@Override
	public void postStop() throws Exception {
		// TODO Auto-generated method stub
		super.postStop();
		LOG.info("user actor" + getSelf().path().name() + " stopped!");
	}

	private void deliverMessage(Object msg, ActorRef sender) {
		String id = messageExtractor.entityId(msg);
		Object payload = messageExtractor.entityMessage(msg);
		if (id == null && id.length() == 0) {
			LOG.warn("Id must not be empty, dropping message [{}]", msg
					.getClass().getName());
			getContext().system().deadLetters().tell(msg, getSelf());
			return;
		}
		if (payload instanceof StartEntity) {
			receiveStartEntity((StartEntity) payload);
			return;
		}

		if (messageBuffers.contains(id)) {
			if (messageBuffers.totalSize() >= bufferSize) {
				LOG.debug("Buffer is full, dropping message for entity [{}]",
						id);
				getContext().system().deadLetters().tell(msg, getSelf());
			} else {
				LOG.debug("Message for entity [{}] buffered", id);
				messageBuffers.append(id, msg, sender);
			}
		} else {
			deliverTo(id, msg, payload, sender);
		}

	}

	private void deliverTo(String id, Object msg, Object payload,
			ActorRef sender) {
		// TODO Auto-generated method stub

	}

	private void receiveStartEntity(StartEntity start) {

		getEntity(start.getEntityId());
		getSender().tell(
				new StartEntityAck(start.getEntityId(), this.shard_id),
				getSelf());
	}

	private void getEntity(String id) {
		String name = id;
		ActorContext context = getContext();
		context.child(name).getOrElse(
				() -> {
					LOG.debug("Starting entity [{}] in shard [{}]", id,
							this.shard_id);
					ActorRef ref = context.actorOf(
							Props.create(entityClazz, id), name);
					context.watch(ref);
					idByRef.put(ref, id);
					refById.put(id, ref);
					return ref;

				});

	}

	// @Override
	// public String persistenceId() {
	// return String.valueOf(id);
	// }

	private Object passivate() {
		// TODO Auto-generated method stub
		return null;
	}

}
