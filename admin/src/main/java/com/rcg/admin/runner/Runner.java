package com.rcg.admin.runner;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rcg.server.StandaloneRunner;

public class Runner {

	public static final String propertyFileName = "configuration.prop";
	
	private static final Logger logger = LoggerFactory.getLogger(Runner.class);

	private static Properties properties;
	
	public static Properties getProperties() {
		return properties;
	}
	
	private static void loadProperties() {
		try {
			if (!new File(propertyFileName).exists()) {
				new File(propertyFileName).createNewFile();
			}
			properties = new Properties();
			properties.load(new FileInputStream(propertyFileName));
		} catch (Exception ex) {
			logger.error("Cant load properties");
		}
	}
	
	public static void main(String... args) {
		loadProperties();
		try {
			JettyRunner.runJetty();
			JavaProcess.exec(StandaloneRunner.class);
		} catch (Exception ex) {
			logger.error("ERROR during starting one of applications:", ex);
		}
	}

}
