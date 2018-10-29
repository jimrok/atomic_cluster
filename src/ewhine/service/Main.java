package ewhine.service;

import java.util.TimeZone;

public class Main {

	private ConfigService config_service = null;
	private ewhine.http.api.HTTPService http_service = null;
	private ActorSystemService actor_service = null;

	public void start() {

		// 启动顺序
		TimeZone timeZone = TimeZone.getTimeZone("Asia/Shanghai");
		TimeZone.setDefault(timeZone);

		// 1.打开配置服务
		config_service = new ConfigService();
		config_service.start();

		// 2.启动Actor 服务
		actor_service = new ActorSystemService();
		actor_service.start();
		
		

		// 启动actor

		// 启动HTTP服务，开放web api请求。
		http_service = new ewhine.http.api.HTTPService();
		http_service.start();

		// 启动Actors

	}

	public void stop() {

		// 关闭http服务
		if (http_service != null) {
			http_service.stop();
		}

		// 关闭actor
		

		if (actor_service != null) {
			actor_service.stop();
		}

		// 关闭配置信息
		if (config_service != null) {
			config_service.stop();
		}

	}

	public static void main(String[] args) {
		Main main = new Main();
		main.start();

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				main.stop();
			}
		});
	}

}
