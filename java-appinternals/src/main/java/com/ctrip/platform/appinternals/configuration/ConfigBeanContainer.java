package com.ctrip.platform.appinternals.configuration;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

public class ConfigBeanContainer {
	private String name;
	
	@XStreamAlias("Components")
	private List<ConfigInfo> beans = new ArrayList<ConfigInfo>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<ConfigInfo> getBeans() {
		return beans;
	}
	public void setBeans(List<ConfigInfo> beans) {
		this.beans = beans;
	}
}
