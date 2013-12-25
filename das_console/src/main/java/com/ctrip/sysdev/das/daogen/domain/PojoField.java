package com.ctrip.sysdev.das.daogen.domain;

public class PojoField {

	private String name;
	
	private boolean isPrimary;
	
	private String type;
	
	private int position;
	
	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	private boolean isValueType;
	
	private boolean nullable;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isPrimary() {
		return isPrimary;
	}

	public void setPrimary(boolean isPrimary) {
		this.isPrimary = isPrimary;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isValueType() {
		return isValueType;
	}

	public void setValueType(boolean isValueType) {
		this.isValueType = isValueType;
	}

	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

}
