package ewhine.cluster.routes;

import akka.actor.ActorRef;
import ewhine.tools.SU;
import ewhine.tools.Secure;

public class ClusterNode {
	public static int UNKNOWN = 0;
	public static int UP = 1;
	public static int DOWN = 2;
	public static int UNREACHABLE = 3;

	private String address;
	private int status = 0;
	private boolean current_self_node = false;
	private String channel = null;
	private ActorRef actor_ref;

	public ClusterNode(String address) {
		this.address = address;
		channel = Secure.sha1Hexdigest(address);

	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int _s) {
		this.status = _s;
	}

	public String getAddress() {
		return address;
	}

	/**
	 * 是否是本机的节点
	 * 
	 * @param b
	 */
	public void setLocalNode(boolean b) {
		this.current_self_node = b;

	}

	@Override
	public String toString() {
		String s = SU.cat("address:",
				this.address + ", is local:" + this.isLocalNode());
		return s;
	}

	public boolean isLocalNode() {
		return this.current_self_node;
	}

}
