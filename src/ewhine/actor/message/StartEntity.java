package ewhine.actor.message;

public class StartEntity {

	private String entity_id;
	private String shard_id;

	public StartEntity(String entityId, String shardId) {
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
