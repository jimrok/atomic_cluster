package ewhine.actor.message;

public class StartEntityAck {

	private String entity_id;
	private String shard_id;

	public StartEntityAck(String entityId, String shardId) {
		this.entity_id = entityId;
		this.shard_id = shardId;
	}

	public String getEntityId() {
		return this.entity_id;
	}

	public String getShardId() {
		return shard_id;
	}

}
