package com.ctrip.platform.dal.appinternals.configuration;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;

public abstract class ConfigBeanBase{

	private Map<String, ConfigName> fields= new ConcurrentHashMap<String, ConfigName>();
	private ConfigInfo info = new ConfigInfo();
	
	public void init() throws Exception {
		Field[] fields = this.getClass().getDeclaredFields();
		for (Field field : fields) {
			Method getMethod = this.getClass().getMethod("get" + StringUtils.capitalize(field.getName()));
			Method setMethod = this.getClass().getMethod(
						"set" + StringUtils.capitalize(field.getName()), field.getType());
			ConfigName pname = new ConfigName();
			pname.setName(field.getName());
			pname.setClazz(field.getType());
			pname.setGetMethod(getMethod);
			pname.setSetMethod(setMethod);
			this.fields.put(field.getName(), pname);
		}
		this.info.setName(this.getClass().getSimpleName());
		this.info.setFullName(this.getClass().getName().replace(".", "-"));
	}
	
	public Collection<ConfigName> getFieldNames() {
		return this.fields.values();
	}

	public ConfigInfo getBeanInfo() {
		return this.info;
	}
	
	public void set(String fieldName, String val) throws Exception{
		ConfigName fname = this.fields.get(fieldName);
		if(fname != null){
			if(!fname.isWrite()){
				throw new Exception("The update field has not matched public set function.");
			}
			if(!fname.getClazz().equals(String.class)){
				throw new Exception("The update field is not String type.");
			}
			fname.getSetMethod().invoke(this, val);
			this.info.setLastModifyTime(new Date());
		}
	}
	
	public String get(String fieldName) throws Exception {
		ConfigName pname = this.fields.get(fieldName);;
		if(null != pname){
			if(!pname.isRead()){
				throw new Exception("The view field has not matched public get function.");
			}
			if(!pname.getClazz().equals(String.class)){
				throw new Exception("The view field is not String type.");
			}
			Object obj = pname.getGetMethod().invoke(this);
			if(null == obj)
				return null;
			return obj.toString();
		}
		return null;
	}
}
