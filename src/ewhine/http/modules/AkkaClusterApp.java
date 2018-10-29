package ewhine.http.modules;

import static spark.Spark.get;
import static spark.Spark.post;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.ModelAndView;
import spark.Request;
import spark.Response;
import ewhine.cluster.routes.ClusterNode;
import ewhine.cluster.routes.ClusterNodeManager;
import ewhine.http.api.AppService;
import ewhine.http.api.VelocityTemplateEngine;
import ewhine.models.User;

public class AkkaClusterApp extends AppService {

	private static final Logger LOG = LoggerFactory
			.getLogger(AkkaClusterApp.class);

	public void start() {

		get("/cluster", (request, response) -> clusterStatus(request, response));

		post("/messages",
				(request, response) -> sendMessages(request, response));

	}

	private Object sendMessages(Request request, Response response) {

		String uid = request.queryParams("user_id");
		// LOG.info("request user_id =>" + uid);
		String msg = request.queryParams("message");
		// LOG.info("request message =>" + request.queryParams("message"));

//		UserActor.EntityEnvelope envelop = new UserActor.EntityEnvelope(
//				Long.parseLong(uid), msg);
//		UserShardingActor.reference().tell(envelop, ActorRef.noSender());
//
//		if (request.queryParams("count") != null) {
//			int count = Integer.parseInt(request.queryParams("count"));
//			for (int i = 0; i < count; i++) {
//				UserShardingActor.reference()
//						.tell(envelop, ActorRef.noSender());
//			}
//		}

		// ActorSystem system = ClusterActorSystem.system();
		// ActorRef counterRegion =
		// ClusterSharding.get(system).shardRegion("Users");
		// counterRegion.tell(envelop, ActorRef.noSender());

		Map<String, Object> model = new HashMap<>();
		Collection<ClusterNode> all_nodes = ClusterNodeManager.instance()
				.getAllNodes();
		model.put("nodes", all_nodes);

		return new VelocityTemplateEngine().render(new ModelAndView(model,
				"template/cluster.vm"));
	}

	public String clusterStatus(Request request, Response response) {
		User c_user = authenticate(request, response);
		// User c_user = User.findByPhoneNumber("18601360472");
		Map<String, Object> model = new HashMap<>();
		model.put("user", c_user);
		Collection<ClusterNode> all_nodes = ClusterNodeManager.instance()
				.getAllNodes();
		model.put("nodes", all_nodes);

		return new VelocityTemplateEngine().render(new ModelAndView(model,
				"template/cluster.vm"));
	}

}
