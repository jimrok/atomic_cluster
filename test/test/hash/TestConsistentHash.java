package test.hash;

import static com.greghaskins.spectrum.Spectrum.afterAll;
import static com.greghaskins.spectrum.Spectrum.beforeAll;
import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;

import java.util.ArrayList;

import org.junit.runner.RunWith;

import com.greghaskins.spectrum.Spectrum;

import ewhine.cluster.routes.ClusterNode;
import ewhine.cluster.routes.ConsistentHash;
import ewhine.cluster.routes.HashFunction;

@RunWith(Spectrum.class)
public class TestConsistentHash {
	{

		describe(
				"一致性hash的测试",
				() -> {

					beforeAll(() -> {

					});

					afterAll(() -> {

					});

					it("无论使用何种方式构建ConsistentHash,不管节点的产生次序是否一致，key的映射应该都是相同的节点",
							() -> {

								ConsistentHash<ClusterNode> ch_a = new ConsistentHash<ClusterNode>(
										new HashFunction(), 2000,
										new ArrayList<ClusterNode>());

								ConsistentHash<ClusterNode> ch_b = new ConsistentHash<ClusterNode>(
										new HashFunction(), 2000,
										new ArrayList<ClusterNode>());

								ClusterNode node1 = new ClusterNode(
										"akka.tcp://akka_cluster@127.0.0.1:2551");
								ClusterNode node2 = new ClusterNode(
										"akka.tcp://akka_cluster@127.0.0.1:2552");
								ClusterNode node3 = new ClusterNode(
										"akka.tcp://akka_cluster@127.0.0.1:2553");

								ClusterNode node4 = new ClusterNode(
										"akka.tcp://akka_cluster@127.0.0.1:2554");

								ch_a.add(node1);
								ch_a.add(node2);
								ch_a.add(node3);
								ch_a.add(node4);

								ch_b.add(node2);
								ch_b.add(node1);
								ch_b.add(node3);
								ch_b.add(node4);

								ClusterNode nda = ch_a.get("user:/1");
								ClusterNode ndb = ch_b.get("user:/1");

								org.junit.Assert.assertTrue(nda.getAddress()
										.equals(ndb.getAddress()));

								nda = ch_a.get("user:/2");
								ndb = ch_b.get("user:/2");

								org.junit.Assert.assertTrue(nda.getAddress()
										.equals(ndb.getAddress()));

								nda = ch_a.get("user:/3");
								ndb = ch_b.get("user:/3");

								org.junit.Assert.assertTrue(nda.getAddress()
										.equals(ndb.getAddress()));

								nda = ch_a.get("user:/10003");
								ndb = ch_b.get("user:/10003");

								org.junit.Assert.assertTrue(nda.getAddress()
										.equals(ndb.getAddress()));

								nda = ch_a.get("user:/234");
								ndb = ch_b.get("user:/234");

								org.junit.Assert.assertTrue(nda.getAddress()
										.equals(ndb.getAddress()));

								nda = ch_a.get("user:/3321");
								ndb = ch_b.get("user:/3321");

								org.junit.Assert.assertTrue(nda.getAddress()
										.equals(ndb.getAddress()));

								nda = ch_a.get("user:/88234");
								ndb = ch_b.get("user:/88234");

								org.junit.Assert.assertTrue(nda.getAddress()
										.equals(ndb.getAddress()));

								nda = ch_a.get("user:/32144");
								ndb = ch_b.get("user:/32144");

								org.junit.Assert.assertTrue(nda.getAddress()
										.equals(ndb.getAddress()));

								nda = ch_a.get("user:/7352");
								ndb = ch_b.get("user:/7352");

								org.junit.Assert.assertTrue(nda.getAddress()
										.equals(ndb.getAddress()));

								nda = ch_a.get("user:/8822");
								ndb = ch_b.get("user:/8822");

								org.junit.Assert.assertTrue(nda.getAddress()
										.equals(ndb.getAddress()));
								
								//--------------------------------------------
								// remove node
								ch_a.remove(node1);
								ch_b.remove(node3);
								ch_a.remove(node3);
								ch_b.remove(node1);
								
								nda = ch_a.get("user:/3321");
								ndb = ch_b.get("user:/3321");

								org.junit.Assert.assertTrue(nda.getAddress()
										.equals(ndb.getAddress()));

								nda = ch_a.get("user:/88234");
								ndb = ch_b.get("user:/88234");

								org.junit.Assert.assertTrue(nda.getAddress()
										.equals(ndb.getAddress()));

								nda = ch_a.get("user:/32144");
								ndb = ch_b.get("user:/32144");

								org.junit.Assert.assertTrue(nda.getAddress()
										.equals(ndb.getAddress()));

								nda = ch_a.get("user:/7352");
								ndb = ch_b.get("user:/7352");

								org.junit.Assert.assertTrue(nda.getAddress()
										.equals(ndb.getAddress()));

								nda = ch_a.get("user:/8822");
								ndb = ch_b.get("user:/8822");

								org.junit.Assert.assertTrue(nda.getAddress()
										.equals(ndb.getAddress()));
								
								
								//--------------------------------------------
								// add node back
								ch_a.add(node1);
								ch_b.add(node1);
								
								nda = ch_a.get("user:/3321");
								ndb = ch_b.get("user:/3321");

								org.junit.Assert.assertTrue(nda.getAddress()
										.equals(ndb.getAddress()));

								nda = ch_a.get("user:/88234");
								ndb = ch_b.get("user:/88234");

								org.junit.Assert.assertTrue(nda.getAddress()
										.equals(ndb.getAddress()));

								nda = ch_a.get("user:/32144");
								ndb = ch_b.get("user:/32144");

								org.junit.Assert.assertTrue(nda.getAddress()
										.equals(ndb.getAddress()));

								nda = ch_a.get("user:/7352");
								ndb = ch_b.get("user:/7352");

								org.junit.Assert.assertTrue(nda.getAddress()
										.equals(ndb.getAddress()));

								nda = ch_a.get("user:/8822");
								ndb = ch_b.get("user:/8822");

								org.junit.Assert.assertTrue(nda.getAddress()
										.equals(ndb.getAddress()));

							});

				});

	}

}
