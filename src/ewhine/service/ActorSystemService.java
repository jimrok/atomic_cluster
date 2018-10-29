package ewhine.service;




public class ActorSystemService {

	public void start() {
		
		UserActorSystem.system();
		
		
	}

	public void stop() {
		
		UserActorSystem.shutdown();
		
	}

}
