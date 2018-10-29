package ewhine.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

public class YamlConfig {

	private static final Logger LOG = LoggerFactory.getLogger(YamlConfig.class);

	Map<String, Object> conf = null;
	private static YamlConfig config = new YamlConfig();

	private YamlConfig() {

		loadYaml();

	}

	@SuppressWarnings("unchecked")
	private void loadYaml() {

		String configPath = System.getProperty("server.root", null);
		File yml_file_path = new File(configPath, "config/application.yml");

		

		try (InputStream input = new FileInputStream(yml_file_path)) {

			Yaml yaml = new Yaml();
			conf = (Map<String, Object>) yaml.load(input);

		} catch (FileNotFoundException e) {
			if(LOG.isErrorEnabled())
				LOG.error("Not found file", e);
		} catch (IOException e1) {
			if(LOG.isErrorEnabled())
				LOG.error("Read file error", e1);
		}

		if(LOG.isInfoEnabled())
			LOG.info("load yml config:{}", yml_file_path);
		
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getAppConfig(String serverEnv) {
		return (Map<String, Object>) config.conf.get(serverEnv);
	}

}
