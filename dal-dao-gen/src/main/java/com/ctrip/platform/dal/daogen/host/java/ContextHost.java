package com.ctrip.platform.dal.daogen.host.java;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.ctrip.platform.dal.daogen.entity.Resource;

public class ContextHost {
	private Map<String, Resource> resources = new HashMap<String, Resource>();
	
	private String docBase = "";
	private String path = "";
	
	
	public Collection<Resource> getResources() {
		return resources.values();
	}
	
	public String getDocBase() {
		return docBase;
	}
	
	public void setDocBase(String docBase) {
		this.docBase = docBase;
	}
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public boolean isEmpty(){
		return this.resources == null || this.resources.isEmpty();
	}
	
	public void addResource(Resource resource){
		if(!this.resources.containsKey(resource.getName())){
			this.resources.put(resource.getName(), resource);
		}
	}
}
