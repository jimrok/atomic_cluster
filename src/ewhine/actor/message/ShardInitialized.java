package ewhine.actor.message;

public class ShardInitialized {

	private String share_id;

	public ShardInitialized(String shardId) {
		this.share_id = shardId;
	}

	public String getShardId() {
		return share_id;
	}

}
