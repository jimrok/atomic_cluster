package ewhine.akka.cluster.sharding.actors;

import akka.actor.ActorRef;

public class BufferdMessage {

	private Object message;
	private ActorRef sender;

	public BufferdMessage(Object msg, ActorRef snd) {
		this.message = msg;
		this.sender = snd;
	}

	public Object getMessage() {
		return message;
	}

	public ActorRef getSender() {
		return sender;
	}

}
