package ewhine.service;

import java.io.File;

import ewhine.tools.SU;

public class ConfigService {
	
	String config_log_file_path = null;
	private static boolean already_config = false;

	public ConfigService() {
		if (already_config) {
			return;
		}
		
		already_config = true;
		
		String serverRoot = System.getProperty("server.root", null);
		File confDir = new File("config");

		if (serverRoot != null) {
			confDir = new File(serverRoot, "config");
		}
		
		if (confDir.exists() ) {
			String pfile = confDir.getAbsolutePath();
			serverRoot = new File(pfile).getParent();
			System.setProperty("server.root", serverRoot);
		}

		config_log_file_path = SU.cat(
				serverRoot, "/config/", "logback.xml");
		
		System.setProperty("logback.configurationFile", config_log_file_path);
		
		System.out.println("Start server from directory:" + serverRoot);
		System.out.println("Load log config file:" + config_log_file_path);
	}

	public void start() {
		
//		System.out.println("load config file in:" + Config.getServerRootDir() + "/config");
	}

	public void stop() {

	}

}
