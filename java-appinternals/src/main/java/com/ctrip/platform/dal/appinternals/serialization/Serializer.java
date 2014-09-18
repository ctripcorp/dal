package com.ctrip.platform.dal.appinternals.serialization;

import java.util.Collection;

import com.ctrip.platform.dal.appinternals.configuration.ConfigBeanBase;

public abstract class Serializer {
	protected String encoding = "UTF-8";
	protected String appPath = "";
	
	public void setEncoding(String encoding){
		this.encoding = encoding;
	}
	
	public void setAppPath(String path){
		this.appPath = path;
	}
	public abstract String serializer(ConfigBeanBase bean) throws Exception;
	public abstract String serializer(Collection<ConfigBeanBase> beans) throws Exception;
}
