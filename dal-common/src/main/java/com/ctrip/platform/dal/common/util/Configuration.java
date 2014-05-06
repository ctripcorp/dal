package com.ctrip.platform.dal.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Configuration {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(Configuration.class);

	private static List<String> defaultResources = new CopyOnWriteArrayList<String>();
	private static ClassLoader classLoader;
	private static Map<String, String> configMap = new HashMap<String, String>();

	static {
		classLoader = Thread.currentThread().getContextClassLoader();
		if (classLoader == null) {
			classLoader = Configuration.class.getClassLoader();
		}
		loadDefaultConfig();
	}

	public static Map<String, String> getAllConfig() {
		return new HashMap<String, String>(configMap);
	}

	public static void addResource(String name) {
		loadConfig(name);
	}

	public static synchronized void addDefaultResource(String name) {
		if (!defaultResources.contains(name)) {
			defaultResources.add(name);
		}
	}

	private static void loadDefaultConfig() {
		for (String defaultResource : defaultResources) {
			loadConfig(defaultResource);
		}
	}

	@SuppressWarnings("unchecked")
	private static void loadConfig(String confFile) {
		Properties props = new Properties();
		InputStream in = null;
		try {
			URL url = classLoader.getResource(confFile);
			if (url == null) {
				return;
			}

			in = url.openStream();
			props.load(in);
			Enumeration<String> en = (Enumeration<String>) props
					.propertyNames();
			while (en.hasMoreElements()) {
				String key = en.nextElement();
				configMap.put(key, props.getProperty(key));
			}
		} catch (Exception e) {
			LOGGER.warn("Cannot load configuration from file <" + confFile
					+ ">", e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ignored) {
				}
			}
		}
	}

	public static String get(String name) {
		return getTrimmed(name);
	}

	public static String get(String name, String defaultValue) {
		String result = get(name);
		if (result == null) {
			result = defaultValue;
		}
		return result;
	}

	private static String getTrimmed(String name) {
		String value = configMap.get(name);
		if (null == value) {
			return null;
		} else {
			return value.trim();
		}
	}

	public static int getInt(String name, int defaultValue) {
		String valueString = get(name);
		if (valueString == null) {
			return defaultValue;
		}
		return Integer.parseInt(valueString);
	}

	public static int getInt(String name) {
		return Integer.parseInt(get(name));
	}

	public static boolean getBoolean(String name, boolean defaultValue) {
		String valueString = get(name);
		if (valueString == null) {
			return defaultValue;
		}
		return Boolean.valueOf(valueString);
	}

	public static boolean getBoolean(String name) {
		return Boolean.valueOf(get(name));
	}

	public static String[] getStrings(String name) {
		String valueString = get(name);
		return getTrimmedStrings(valueString);
	}

	public static Class<?> getClass(String name) throws ClassNotFoundException {
		String valueString = getTrimmed(name);
		if (valueString == null) {
			throw new ClassNotFoundException("Class " + name + " not found");
		}
		return Class.forName(valueString, true, classLoader);
	}

	public static Class<?>[] getClasses(String name)
			throws ClassNotFoundException {
		String[] classNames = getStrings(name);
		if (classNames == null) {
			return null;
		}
		Class<?>[] classes = new Class<?>[classNames.length];
		for (int i = 0; i < classNames.length; i++) {
			classes[i] = getClass(classNames[i]);
		}
		return classes;
	}
/*
	@SuppressWarnings("unchecked")
	public static <U> List<U> getInstances(String name, Class<U> xface)
			throws ClassNotFoundException {
		List<U> ret = new ArrayList<U>();
		Class<?>[] classes = getClasses(name);
		for (Class<?> cl : classes) {
			if (!xface.isAssignableFrom(cl)) {
				throw new RuntimeException(cl + " does not implement " + xface);
			}
			ret.add((U) ReflectionUtil.newInstance(cl));
		}
		return ret;
	}
*/
	public static void dumpDeprecatedKeys() {
		for (String key : configMap.keySet()) {
			System.out.println(key + "=" + configMap.get(key));
		}
	}

	public final static String[] emptyStringArray = {};
	public static String[] getTrimmedStrings(String str) {
		if (null == str || "".equals(str.trim())) {
			return emptyStringArray;
		}
		return str.trim().split("\\s*,\\s*");
	}
	
	public final static String KEY_SEPARATOR = ".";

	public static String buildKey(String...keys) {
		StringBuilder sb = new StringBuilder();
		for(String key: keys)
			sb.append(key).append(KEY_SEPARATOR);
		return sb.deleteCharAt(sb.length() - 1).toString();
	}
}
