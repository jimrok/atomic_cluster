package ewhine.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class Config {
	// private static Logger log = LoggerFactory.getLogger(Config.class);
	private Properties props = new Properties();
	private static String serverRoot = null;

	private Map<String, Object> configMap = null;
	private static String serverEnv = "development";

	private static ConfigSet defaultConfig = null;
	private static String content_path = null;

	static {
		// serverEnv = System.getProperty("server.root", "development");

		if ("production".equals(System.getProperty("server.env"))) {
			serverEnv = "production";
		} else {
			System.setProperty("server.env", serverEnv);
		}

		
		serverRoot = System.getProperty("server.root", null);
		File confDir = new File("config");

		if (serverRoot != null) {
			confDir = new File(serverRoot, "config");
		}

		File yamlConfigFile = new File(confDir, "application.yml");

		if (confDir.exists() && yamlConfigFile.exists()) {
			String pfile = confDir.getAbsolutePath();
			serverRoot = new File(pfile).getParent();
		}

		Map<String, Object> appConfig = YamlConfig.getAppConfig(serverEnv);

		defaultConfig = new ConfigSet(appConfig);

	}

	public static String getServerEnv() {
		return serverEnv;
	}

	public static String getServerRootDir() {
		return serverRoot;
	}

	public static String getContentsPath() {
		// TODO generate right contents path.
		if (content_path == null) {
			content_path = Config.getPropertyConfig("server.properties").get(
					"contents_file_path");
		}
		return content_path;
	}

	private Config(Map<String, Object> _configMap) {
		this.configMap = _configMap;
	}

	private Config(String fileName) {
		try {
			String confDirName = System.getProperty("conf.dir");
			File confDir = null;
			if (confDirName == null) {
				confDir = new File("config");
			} else {
				confDir = new File(confDirName);
			}
			
//			String property_file_name = fileName;  //+ ".properties";
			File serverPropFile = new File(confDir, fileName);
			if (confDir.exists() && serverPropFile.exists()) {
				FileInputStream fin = null;
				try {
					fin = new FileInputStream(serverPropFile);
					props.load(fin);
				} catch (Exception e) {

					System.err
							.println("propblem loading conf file, using default settings...");
					e.printStackTrace(System.err);
				} finally {
					if (fin != null) {
						fin.close();
					}
				}
			}
		} catch (IOException e) {

			System.err.println("Read config file error!");
			e.printStackTrace(System.err);
		}
	}

	public static Config getPropertyConfig(String name) {
		return new Config(name);
	}

	public String get(String name) {
		return props.getProperty(name);
	}

	public String get(String name, String defaultValue) {
		return props.getProperty(name, defaultValue);
	}

	public Properties getProps() {
		return props;
	}

	public int getIntValue(String _key, int defaultValue) {
		Object value = defaultConfig.getIntValue(_key);
		if (value == null) {
			return defaultValue;
		}

		return (int) value;
	}

	public int getIntValue(String key) {
		return getIntValue(key, 0);
	}

	public static boolean getBooleanValue(String _key) {
		return getBooleanValue(_key, false);
	}

	public static String getValue(String key, String defaultValue) {
		Object value = defaultConfig.getValue(key);
		if (value == null) {
			return defaultValue;
		}
		return value.toString();
	}
	
	public static void setValue(String key,Object value) {
		defaultConfig.setValue(key,value);
	}

	public static String getValue(String key) {
		return getValue(key, null);
	}

	public static Map<String, Object> getConfig(String key) {

		Map<String, Object> value = (Map<String, Object>) defaultConfig
				.getConfig(key);

		if (value != null) {
			return value;
		}
		return null;
	}

	public static boolean getBooleanValue(String _key, boolean defaultValue) {
		Object value = defaultConfig.getBooleanValue(_key,defaultValue);
		if (value == null) {
			return defaultValue;
		}

		return (boolean) value;
	}

	public static void main(String[] args) {

		// boolean b = Config.getBooleanValue("build_search_index");
		// String c = Config.getValue("remote_push_server_url");
		// System.out.println("config:" + b);
		// System.out.println("config:" + c);
		// Map mail = Config.getConfig("smtp_settings");
		// System.out.println("host:" + mail.get("host"));

		Config config = Config.getPropertyConfig("memcached.properties");
		System.out.println("config:"
				+ config.get("memcached.server", "127.0.0.1:11211"));
		String secret_token = Config.getPropertyConfig(
				"secret_token.properties").get("secret_token");
		System.out.println("secret_token:" + secret_token);
		System.out.println("faye_host:" + Config.getValue("faye_host"));
	}

}
