package ewhine.actor.message;

import akka.actor.ActorRef;

public class Terminated {

	private ActorRef ref;

	public Terminated(ActorRef terminate_ref) {
		this.ref = terminate_ref;
	}

	public ActorRef getRef() {
		return ref;
	}

}
