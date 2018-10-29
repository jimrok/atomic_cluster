package ewhine.cluster.routes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

import akka.actor.ActorRef;

public class ClusterNodeManager {

	private HashMap<String, ClusterNode> c_nodes = new HashMap<String, ClusterNode>();

	private AtomicReference<Collection<ClusterNode>> ref_nodes = new AtomicReference<Collection<ClusterNode>>();
	private ConsistentHash<ClusterNode> dh_nodes = new ConsistentHash<ClusterNode>(
			new HashFunction(), 2000, new ArrayList<ClusterNode>());

	private ClusterNode local_node = null;
	private HashMap<String, ActorRef> localRefs = new HashMap<String, ActorRef>();

	private static class SingletonHolder {
		private static final ClusterNodeManager INSTANCE = new ClusterNodeManager();
	}

	public static ClusterNodeManager instance() {
		return SingletonHolder.INSTANCE;
	}

	public ClusterNodeManager() {
		ref_nodes.lazySet(new ArrayList<ClusterNode>());
	}

	public void addNode(ClusterNode node) {
		c_nodes.put(node.getAddress(), node);
		Collection<ClusterNode> all_nodes = c_nodes.values();
		ref_nodes.lazySet(all_nodes);
		dh_nodes.add(node);

		if (node.isLocalNode()) {
			this.local_node = node;
		}
	}

	
	public void removeNode(String node_address) {
		ClusterNode node = c_nodes.remove(node_address);
		Collection<ClusterNode> all_nodes = c_nodes.values();
		ref_nodes.lazySet(all_nodes);
		dh_nodes.remove(node);
	}

	public ClusterNode getNode(String address) {
		return c_nodes.get(address);
	}

	public ClusterNode getLocalNode() {
		return local_node;
	}

	public Collection<ClusterNode> getAllNodes() {
		Collection<ClusterNode> current_nodes = ref_nodes.get();
		return current_nodes;
	}

	/**
	 * 查找key对应的Node
	 * 
	 * @param key
	 * @return
	 */
	public ClusterNode lookupMessageRouteNode(String key) {
		ClusterNode node = dh_nodes.get(key);
		return node;
	}

	
}
