# akka cluster

##资料
文章介绍了构建一个群集的例子。强调一点，seed的节点第一个必须先启动，否则其他的节点无法加入，会形成分裂的群集。
[akka的群集](http://blog.csdn.net/beliefer/article/details/53893929)

对于分布式，一致性hash的算法
[构建一个群集的hash算法](https://segmentfault.com/a/1190000009565519)


##设计

akka的群集的publish和subscribe可以看这个文档
[https://doc.akka.io/docs/akka/2.5/distributed-pub-sub.html](https://doc.akka.io/docs/akka/2.5/distributed-pub-sub.html)

可以考虑基于这个特性实现akka的discribute actor。
akka本身也有[sharding](https://doc.akka.io/docs/akka/2.5/cluster-sharding.html)，但比较复杂，可能自己实现比较好。这都需要实现后进行测试。

设计方案

1. actor的uri进行一致性hash处理，映射到生产的节点上。如果一个消息发给的actor是本地的actor，则直接使用ActorRef进行投递。如果是一个外部的Node，则用Publish，Subscription投递。

2. Publish的和Subcription最多只投递一次，因此需要扩展协议，实现ack，保证消息一定送达。

3. 对于群集节点的上线和下线，将影响一致性hash的映射，这个过程比较复杂。也许设计犯案不唯一。

4. 上线新节点的实现

新节点上线后，其他节点得到通知，等待新节点的加入。
发往新节点的actor消息会暂时放入队列里缓存。
新节点发送reblance的通知。
群集节点收到reblance通知后，查找需要映射到新节点的actor，关闭这些actor。

新节点等待全部的节点完成reblance，将队列里缓存的消息进行处理。
完成之后，则打开HTTP的直连通道。


5. 旧节点下线

旧节点A关闭HTTP的直连通道
旧节点A广播stopping的消息给群集其他节点。
其他节点将发往A的消息临时缓存在一个队列中。

A开始关闭全部的actor，然后
A下线，退出群集
旧节点收到A退出群集的消息后，从队列里取出准备发给A的消息，重新计算Node，转发给新node