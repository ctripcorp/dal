package com.ctrip.platform.daogen;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import com.mongodb.MongoClient;

public enum MongoClientManager {

	INSTANCE;

	public static MongoClient getDefaultMongoClient() {
		return INSTANCE.client;
	}

	private MongoClientManager() {
		InputStream in = null;
		props = new Properties();

		try {
			ClassLoader defaultLoader = Thread.currentThread()
					.getContextClassLoader();
			if (defaultLoader == null) {
				defaultLoader = MongoClientManager.class.getClassLoader();
			}

			classLoader = defaultLoader;

			URL url = classLoader.getResource("conf.properties");
			if (url == null) {
				return;
			}

			in = url.openStream();
			props.load(in);

			client = new MongoClient(props.getProperty("mongoHost"),
					Integer.parseInt(props.getProperty("mongoPort")));

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ignored) {
				}
			}
		}

	}

	private MongoClient client;

	private Properties props;

	private ClassLoader classLoader;

}
