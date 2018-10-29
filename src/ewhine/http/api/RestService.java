package ewhine.http.api;

import static spark.Spark.halt;

import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.Request;
import spark.Response;
import ewhine.tools.SU;

public abstract class RestService {

	private static final Logger LOG = LoggerFactory
			.getLogger(RestService.class);

	final static public String CURRENT_USER = "CURRENT_USER";

	

	public Object getFromSession(Request request, String session_key) {
		return request.session().attribute(session_key);
	}

	public Integer params(Request request, String name, Integer defaultValue) {

		try {
			String v = request.queryParams(name);
			if (v != null) {
				return Integer.valueOf(v);
			} else {
				return defaultValue;
			}
		} catch (Throwable t) {
			return 0;
		}
	}

	public Long params(Request request, String name) {
		String v = request.queryParams(name);
		if (v != null) {
			return Long.valueOf(v);
		} else {
			return null;
		}
	}

	public String params(Request request, String name, String defaultValue) {
		String v = request.queryParams(name);
		if (v != null) {
			return v;
		} else {
			return defaultValue;
		}
	}

	public static void error(int http_code, String message, Request request) {
		Object user_id = request.attribute("request_tag");

		if (LOG.isErrorEnabled()) {
			StringBuilder params = new StringBuilder();
			Map<String, String[]> _map = request.queryMap().toMap();
			for (String k : _map.keySet()) {
				params.append(k);
				params.append("=");
				String[] _o = _map.get(k);
				if (_o != null) {
					params.append(String.join(",", _o));
				}
				params.append("&");
			}
			if (params.length() > 0) {
				params.deleteCharAt(params.length() - 1);
			}
			String headers = java.lang.String.join(", ", request.headers()
					.stream().map(x -> {
						return x + ":" + request.headers(x);
					}).collect(Collectors.toList()));

			LOG.error(SU.cat("[user:", user_id, "] ", request.requestMethod(),
					" ", request.url(), " params[", params.toString(), "]",
					" Remote-IP:", request.ip(), " X-Real-IP:",
					request.headers("X-Real-IP"), " headers[", headers, "]",
					" - error: ", http_code, " ", message));
		}

		halt(http_code, "{\"errors\":{\"code\":" + http_code
				+ ",\"message\":\"" + message + "\"}}");
	}

	
	public static void logEorror(String logTag, Request request,
			Response response, Exception exception) {
		if (LOG.isErrorEnabled()) {

			StringBuilder params = new StringBuilder();
			Map<String, String[]> _map = request.queryMap().toMap();
			for (String k : _map.keySet()) {
				params.append(k);
				params.append("=");
				String[] _o = _map.get(k);
				if (_o != null) {
					params.append(String.join(",", _o));
				}
				params.append("&");
			}
			if (params.length() > 0) {
				params.deleteCharAt(params.length() - 1);
			}
			String headers = java.lang.String.join(", ", request.headers()
					.stream().map(x -> {
						return x + ":" + request.headers(x);
					}).collect(Collectors.toList()));
			StringBuilder sb = new StringBuilder();
			sb.append("Server error:[").append(logTag).append("] ")
					.append(request.requestMethod()).append(" ")
					.append(request.url()).append(" params[")
					.append(params.toString()).append("] IP:")
					.append(request.headers("X-Real-IP")).append(" headers[")
					.append(headers).append("]\n");

			LOG.error(sb.toString(), exception);
		}
	}



}
