package ewhine.http.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.Request;
import spark.Response;
import ewhine.models.User;

public class AppService extends RestService {
	private static final Logger LOG = LoggerFactory.getLogger(AppService.class);

	public User getCurrentUser(Request request) {

		User account = request.attribute(CURRENT_USER);
		return account;

	}

	public static int getInt(Request request, String name, int default_value) {
		String value = request.queryParams(name);
		if (value == null || value.length() == 0) {
			return default_value;
		}

		int v = Integer.parseInt(value);
		if (v < default_value) {
			return default_value;
		}
		return v;
	}

	public static User authenticate(Request request, Response response) {
		User c_user = null;

		return c_user;
	}

}
