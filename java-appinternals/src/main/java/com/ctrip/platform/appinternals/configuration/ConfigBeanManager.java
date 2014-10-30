package com.ctrip.platform.appinternals.configuration;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.platform.appinternals.persistence.Storage;

public class ConfigBeanManager {
	private static Logger logger = LoggerFactory.getLogger(ConfigBeanManager.class);
	private static Map<String, ConfigBeanBase> cache = null;
	private static Map<String, String> alais = null;
	private static Storage storage = null;
	
	static{
		cache = new ConcurrentHashMap<String, ConfigBeanBase>();
		alais = new ConcurrentHashMap<String, String>();
		String classPath = Thread.currentThread().getContextClassLoader()
				.getResource("").getPath();
		storage = new Storage(classPath);
		storage.load();
	}
	
	public static void register(ConfigBeanBase... beans) throws Exception{
		for (ConfigBeanBase bean : beans) {
			bean.init();
			cache.put(bean.getBeanInfo().getFullName(), bean);
			if(bean.getBeanInfo().getAlias() != null && !bean.getBeanInfo().getAlias().isEmpty()){
				alais.put(bean.getBeanInfo().getAlias(), bean.getBeanInfo().getFullName());
			}
		}
		load(Arrays.asList(beans));
	}
	
	public static void setPropertiesPath(String path){
		storage = new Storage(path);
		storage.load();
	}
	
	public static Map<String, ConfigBeanBase> getBeans(){
		return cache;
	}
	
	public static ConfigBeanBase getBean(String name){
		String key = alais.get(name);
		return key != null && !key.isEmpty() ? cache.get(key) :cache.get(name);
	}
	
	public static void load(Collection<ConfigBeanBase> beans){
		for (ConfigBeanBase bean : beans) {
			String className = bean.getBeanInfo().getFullName();
			String propName = "";
			for(ConfigName field : bean.getFieldNames()){
				propName = className + "." + field.getName();
				String propVal = storage.get(propName);
				if(null == propVal){
					try {
						storage.set(propName, bean.get(field.getName()));
					} catch (Exception e) {
						logger.error(String.format("Save field[%] for bean[%s] failed",
								field.getName(), className));
					}
				}else{
					try {
						if(field.getSetMethod() != null)
							bean.set(field.getName(), propVal);
					} catch (Exception e) {
						logger.error(String.format("Load field[%s] for bean[%s] failed",
								field.getName(), className));
					}		
				}
			}
		}
		storage.save();
		
		logger.info(String.format("Load config beans from %s completed", storage.getPath()));
	}
	
	public static void save(ConfigBeanBase bean, String fieldName){
		String propName = bean.getBeanInfo().getFullName() + "." + fieldName;
		try {
			storage.set(propName, bean.get(fieldName));
		} catch (Exception e) {
			logger.error(String.format("Save field[%] for bean[%s] failed",
					fieldName, bean.getBeanInfo().getFullName()));
		}
		
		storage.save();
	}
}
