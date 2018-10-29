package ewhine.http.api;

import static spark.Spark.before;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ewhine.config.Config;
import ewhine.http.modules.AkkaClusterApp;
import ewhine.tools.SU;

public class HTTPService {
	private static final Logger LOG = LoggerFactory
			.getLogger(HTTPService.class);

	private final Main smain = new Main();

	public void start() {
		Runnable _runconfig = () -> {

			before("/*",
					(request, response) -> {

						String req_uri = request.uri();
						if (req_uri != null
								&& (req_uri.startsWith("/m_centre") || req_uri
										.startsWith("/"))) { // .startsWith("/product")
							// include
							// .startsWith("/products")
							return;
						}

						AppService.authenticate(request, response);

					});

			spark.Spark.exception(Exception.class, (exception, request,
					response) -> {

				LOG.error(SU.cat("api reqeust error", ",message:",
						exception.getMessage()));
			});

			// 是否对静态文件进行处理
			String static_dir = Config.getValue("service_static");
			if (static_dir != null) {
				File public_file_dir = new File(static_dir);
				if (public_file_dir.exists()) {

					spark.Spark.staticFiles.externalLocation(public_file_dir
							.getAbsolutePath());
					LOG.info("service static file:"
							+ public_file_dir.getAbsolutePath());
				}
			}

			// -------------------User API ----------------------------------
			AkkaClusterApp clusterAPI = new AkkaClusterApp();
			clusterAPI.start();

			LOG.info("server load app config finished.");
		};

		smain.config(_runconfig);
		smain.start();

	}

	public void stop() {
		smain.stop();
	}

}
