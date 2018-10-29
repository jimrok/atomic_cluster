package ewhine.akka.cluster.sharding.actors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import akka.actor.ActorRef;

public class ShardBuffer {
	private HashMap<String, List<BufferdMessage>> shardBuffers = new HashMap<>();
	private long total = 0;

	public boolean contains(String shardId) {
		return shardBuffers.containsKey(shardId);
	}

	public List<BufferdMessage> getOrEmpty(String shardId) {
		// 获得这个shard的buffer的message。
		List<BufferdMessage> messages = shardBuffers.get(shardId);
		if (messages == null) {
			messages = new ArrayList<BufferdMessage>();
			shardBuffers.put(shardId, messages);
		}

		return messages;
	}

	public void append(String shard_id, Object message, ActorRef snd) {
		List<BufferdMessage> messags = getOrEmpty(shard_id);
		messags.add(new BufferdMessage(message, snd));
		total++;
	}

	public void remove(String shard_id) {
		List<BufferdMessage> messages = getOrEmpty(shard_id);
		shardBuffers.remove(shard_id);
		total -= messages.size();
	}
	
	public long getTotalSize() {
		return total;
	}

}
