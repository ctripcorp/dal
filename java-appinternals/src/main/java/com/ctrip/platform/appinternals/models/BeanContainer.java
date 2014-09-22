package com.ctrip.platform.appinternals.models;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("ComponentCollection")
public class BeanContainer {
	private String name;
	
	@XStreamAlias("Components")
	private List<BeanView> beans = new ArrayList<BeanView>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<BeanView> getBeans() {
		return beans;
	}
	public void setBeans(List<BeanView> beans) {
		this.beans = beans;
	}
}
