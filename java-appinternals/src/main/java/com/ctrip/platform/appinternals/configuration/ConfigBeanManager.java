package com.ctrip.platform.appinternals.configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigBeanManager {
	private static Map<String, ConfigBeanBase> cache = null;
	private static Map<String, String> alais = null;
	
	static{
		cache = new ConcurrentHashMap<String, ConfigBeanBase>();
		alais = new ConcurrentHashMap<String, String>();
	}
	
	public static void register(ConfigBeanBase... beans) throws Exception{
		for (ConfigBeanBase bean : beans) {
			bean.init();
			cache.put(bean.getBeanInfo().getFullName(), bean);
			if(bean.getBeanInfo().getAlias() != null && !bean.getBeanInfo().getAlias().isEmpty()){
				alais.put(bean.getBeanInfo().getAlias(), bean.getBeanInfo().getFullName());
			}
		}
	}
	
	public static Map<String, ConfigBeanBase> getBeans(){
		return cache;
	}
	
	public static ConfigBeanBase getBean(String name){
		String key = alais.get(name);
		return key != null && !key.isEmpty() ? cache.get(key) :cache.get(name);
	}
}
