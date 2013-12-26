package com.ctrip.sysdev.das.daogen.domain;

public class ZTreeElement {
	
	private String id;
	
//	private String namespace;
	
	private String name;
	
	private boolean isParent;
	
	private String type;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public boolean getIsParent() {
		return isParent;
	}

	public void setIsParent(boolean isParent) {
		this.isParent = isParent;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	

}
