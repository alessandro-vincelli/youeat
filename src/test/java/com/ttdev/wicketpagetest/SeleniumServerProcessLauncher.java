package com.ttdev.wicketpagetest;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SeleniumServerProcessLauncher {
	private static final String WPT_PROPERTIES = "wpt.properties";
	private static Logger LOGGER = LoggerFactory
			.getLogger(SeleniumServerProcessLauncher.class);
	private ConsoleLauncher consoleLauncher;

	public void start(Configuration cfg) {
		String pathToJar = getPathToSeleniumServerJar();
		if (pathToJar == null) {
			LOGGER
					.warn("Path to selenium server jar not found. You may want to create "
							+ "wpt.properties in your home to define the path.");
			return;
		}
		String[] cmd = new String[] { "java", "-jar", pathToJar, "-port",
				Integer.toString(cfg.getSeleniumServerPort()) };
		LOGGER.info(
				"Executing {} {} {} in a console to launch selenium server",
				cmd);
		consoleLauncher = new ConsoleLauncher();
		consoleLauncher.launchConsoleToExecute(cmd);
	}

	public void stop() {
		if (consoleLauncher != null) {
			consoleLauncher.stop();
		}
	}

	private String getPathToSeleniumServerJar() {
		File file = new File(System.getProperty("user.home"), WPT_PROPERTIES);
		if (file.exists()) {
			try {
				InputStream in = new FileInputStream(file);
				try {
					Properties props = new Properties();
					props.load(in);
					String seleniumServerJar = props
							.getProperty("selenium.server.jar");
					return seleniumServerJar;
				} finally {
					in.close();
				}
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}
}
