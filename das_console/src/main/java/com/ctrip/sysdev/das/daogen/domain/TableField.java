package com.ctrip.sysdev.das.daogen.domain;

public class TableField {
	
	private String name;
	
	private boolean indexed;
	
	private boolean primary;

	public boolean isPrimary() {
		return primary;
	}

	public void setPrimary(boolean primary) {
		this.primary = primary;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isIndexed() {
		return indexed;
	}

	public void setIndexed(boolean indexed) {
		this.indexed = indexed;
	}

}
