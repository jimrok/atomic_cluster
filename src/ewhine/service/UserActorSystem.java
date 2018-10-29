package ewhine.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.concurrent.Future;
import akka.actor.ActorSystem;
import akka.actor.Terminated;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class UserActorSystem {
	final static private Logger LOG = LoggerFactory
			.getLogger(UserActorSystem.class.getName());
	private static ActorSystem _system = null;

	public static ActorSystem system() {
		if (_system == null) {
			synchronized (UserActorSystem.class) {
				 Config config = ConfigFactory.load();
				// Config config = ConfigFactory.parseFile(new
				// File(System.getProperty("server.root") +
				// "/config/application.conf"));
//				Config config = ConfigFactory.parseFile(new File(
//						ewhine.config.Config.getServerRootDir(),
//						"/config/application.conf"));
//				config.resolve();
				
				ActorSystem system = ActorSystem.create("ewhine", config);
				_system = system;

//				final ActorRef actor = system.actorOf(Props
//						.create(DeadLetterActor.class));
//				system.eventStream().subscribe(actor, DeadLetter.class);
			}

		}
		return _system;
	}

	public static void shutdown() {
		if (_system != null) {
			Future<Terminated> f = _system.terminate();
			if (f.isCompleted()) {
				if (LOG.isInfoEnabled())
					LOG.info("actor system stoped!");
			}
			_system = null;
		}
	}

}
