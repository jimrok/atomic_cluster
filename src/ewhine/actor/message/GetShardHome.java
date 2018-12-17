package ewhine.actor.message;

import java.io.Serializable;

public class GetShardHome extends CoordinatorCommand implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String share_id;

	public GetShardHome(String shardId) {
		this.share_id = shardId;
	}

	public String getShardId() {
		return share_id;
	}

}
