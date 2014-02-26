package com.ctrip.platform.dal.daogen.pojo;

public class W2uiElement {
	
	private String currentId;
	
	private boolean isParent;
	
	private String relativeName;
	
	private String name;


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCurrentId() {
		return currentId;
	}

	public void setCurrentId(String currentId) {
		this.currentId = currentId;
	}

	public boolean isParent() {
		return isParent;
	}

	public void setParent(boolean isParent) {
		this.isParent = isParent;
	}

	public String getRelativeName() {
		return relativeName;
	}

	public void setRelativeName(String relativeName) {
		this.relativeName = relativeName;
	}

}
