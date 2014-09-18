package com.ctrip.platform.dal.appinternals.configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigBeanManager {
	private static Map<String, ConfigBeanBase> cache = null;
	
	static{
		cache = new ConcurrentHashMap<String, ConfigBeanBase>();
	}
	
	public static void register(ConfigBeanBase... beans) throws Exception{
		for (ConfigBeanBase bean : beans) {
			bean.init();
			cache.put(bean.getBeanInfo().getFullName(), bean);
		}
	}
	
	public static Map<String, ConfigBeanBase> getBeans(){
		return cache;
	}
	
	public static ConfigBeanBase getBean(String key){
		return cache.get(key);
	}
}
