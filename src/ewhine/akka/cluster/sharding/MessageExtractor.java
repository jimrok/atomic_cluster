package ewhine.akka.cluster.sharding;

public interface MessageExtractor {

	public String entityId(Object message);

	public Object entityMessage(Object message);

	public String shardId(Object message);

}
