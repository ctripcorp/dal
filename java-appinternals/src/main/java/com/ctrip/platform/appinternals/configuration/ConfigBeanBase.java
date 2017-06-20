package com.ctrip.platform.appinternals.configuration;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ctrip.platform.appinternals.annotations.BeanMeta;
import com.ctrip.platform.appinternals.helpers.Converter;
import com.ctrip.platform.appinternals.helpers.Helper;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

public abstract class ConfigBeanBase{
	
	@XStreamOmitField
	private Map<String, ConfigName> fields= new ConcurrentHashMap<String, ConfigName>();
	@XStreamOmitField
	private Map<String, String> aliases = new HashMap<String, String>();
	@XStreamOmitField
	private Map<String, ChangeEvent> events = new ConcurrentHashMap<String, ChangeEvent>();
	@XStreamOmitField
	private ConfigInfo info = new ConfigInfo();

	public void init() throws Exception {
		Field[] fields = this.getClass().getDeclaredFields();
		for (Field field : fields) {
			BeanMeta falias = field.getAnnotation(BeanMeta.class);
			if((falias != null && falias.omit()) || Modifier.isStatic(field.getModifiers()))
				continue;
			Method getMethod = null;
			Method setMethod = null;
			try{
				if(field.getType().equals(boolean.class) || field.getType().equals(Boolean.class))
					getMethod = this.getClass().getMethod("is" + Helper.capitalize(field.getName()));
				else
					getMethod = this.getClass().getMethod("get" + Helper.capitalize(field.getName()));
				setMethod = this.getClass().getMethod(
							"set" + Helper.capitalize(field.getName()), field.getType());
			}catch(NoSuchMethodException ex){ }
			ConfigName pname = new ConfigName();
			pname.setName(field.getName());
			
			if(falias !=null)
				pname.setPersistence(falias.persistence());
			
			pname.setClazz(field.getType());
			pname.setGetMethod(getMethod);
			pname.setSetMethod(setMethod);

			if(falias != null && !falias.alias().isEmpty()){
				pname.setAlias(falias.alias());
				this.aliases.put(falias.alias(), pname.getName());
			}	
			this.fields.put(field.getName(), pname);
		}
		BeanMeta alias = this.getClass().getAnnotation(BeanMeta.class);
		if(alias != null){
			this.info.setAlias(alias.alias());
			this.info.setPersistence(alias.persistence());
		}
		this.info.setName(this.getClass().getSimpleName());
		
		this.info.setFullName(this.getClass().getName().replace(".", "-"));
	}
	
	public void addChangeEvent(String fieldName, ChangeEvent e){
		this.events.put(fieldName, e);
	}
	
	public Collection<ConfigName> getFieldNames() {
		return this.fields.values();
	}

	public ConfigInfo getBeanInfo() {
		return this.info;
	}
	
	public synchronized void set(String fieldName, String val) throws Exception{
		val = null == val ? "" : val; //Default
		String key = this.aliases.get(fieldName);
		ConfigName fname = key != null ? this.fields.get(key) : this.fields.get(fieldName);
		if(fname != null){
			if(!fname.isWrite()){
				throw new Exception("The update field has not matched public set function.");
			}
			Object oldVal = fname.getGetMethod().invoke(this, new Object[]{});
			Object newVal = Converter.convert(val, fname.getClazz());
			ChangeEvent event = this.events.containsKey(fname.getName()) ? 
					this.events.get(fname.getName()) : null;
			if(null != event){
				event.before(oldVal, val);
			}
			
			fname.getSetMethod().invoke(this, newVal);
			
			if(null != event){
				event.end(oldVal, val);
			}

			this.info.setLastModifyTime(new Date());
			if(fname.isPersistence() || this.info.isPersistence())
				ConfigBeanManager.save(this, fname.getName());
		}else{
			throw new Exception("The update field[" + fieldName + "] name or alias doesn't exist.");
		}
	}
	
	public String get(String fieldName) throws Exception {
		String key = this.aliases.get(fieldName);
		ConfigName pname = key != null ? this.fields.get(key) : this.fields.get(fieldName);
		if(null != pname){
			if(!pname.isRead()){
				throw new Exception("The view field has not matched public get function.");
			}
			Object obj = pname.getGetMethod().invoke(this);
			if(null == obj)
				return null;
			return obj.toString();
		}else{
			throw new Exception("The specified field[" + fieldName+ "] name or alias doesn't exist.");
		}
	}
}