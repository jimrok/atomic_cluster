package ewhine.http.api;

import java.io.File;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.Request;
import spark.Response;
import ewhine.config.Config;
import ewhine.tools.SU;

public class Main {

	private static final Logger LOG = LoggerFactory.getLogger(Main.class);
	private Runnable _run = null;

	public Main() {

	}

	public void config(Runnable run) {
		this._run = run;
	}

	public void start() {

		int maxThreads = Integer.parseInt(Config.getPropertyConfig(
				"server.properties").get("server.max_thread", "32"));
		int minThreads = 2;
		int timeOutMillis = 30000;

		
		
		int port = Integer.parseInt(Config.getPropertyConfig(
				"server.properties").get("http.port", "3001"));

		spark.Spark.port(port);
		spark.Spark.threadPool(maxThreads, minThreads, timeOutMillis);
		
		String public_file = Config.getServerRootDir() + File.separator
				+ "public";
		File public_file_dir = new File(public_file);
		if (public_file_dir.exists()) {
			spark.Spark.staticFiles.externalLocation(public_file);
		}

		if (_run != null) {
			_run.run();
			LOG.info("load run code finished!");
		}

		spark.Spark.afterAfter((request, response) -> {

			String requestTag = getLogTag(request);
			logRequest(requestTag, request, response);

		});

		spark.Spark.exception(Exception.class,
				(exception, request, response) -> {
					String requestTag = getLogTag(request);
					RestService.logEorror(requestTag, request, response,
							exception);
					response.body("Server error!");
					response.status(500);

				});
		LOG.info("load exception process.");


		spark.Spark.awaitInitialization();

		if (LOG.isInfoEnabled())
			LOG.info(SU.cat("server started. port:", port,", max_thread:",maxThreads));
		// LOG.info("server started. port:{}", port);
	}

	public void stop() {

		spark.Spark.stop();

	}

	public String getLogTag(Request request) {
		Object rtag = request.attribute("request_tag");

		if (rtag == null) {
			return "";
		}

		return rtag.toString();
	}

	public void logRequest(String tag, Request request, Response response) {
		if (LOG.isInfoEnabled()) {
			StringBuilder params = new StringBuilder();
			Map<String, String[]> _map = request.queryMap().toMap();

			_map.forEach((k, v) -> {
				params.append(k);
				params.append("=<");

				if ("password".equals(k)) {
					params.append("******>, ");
					return;
				}

				String[] _o = _map.get(k);
				if (_o != null) {
					params.append(String.join(",", _o));
				}
				params.append(">, ");

			});

			if (params.length() > 0) {
				params.deleteCharAt(params.length() - 2);
			}
			LOG.info(SU.cat(tag, request.requestMethod(), " ",
					response.status(), " ", request.url(), " params[",
					params.toString(), "] ", request.headers("X-Real-IP")));
		}
	}

	public static void main(String[] args) {

	}

}
