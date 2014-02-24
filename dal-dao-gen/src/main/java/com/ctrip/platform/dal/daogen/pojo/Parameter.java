package com.ctrip.platform.dal.daogen.pojo;

public class Parameter {
	
	private String type;
	
	private String name;
	
	private String fieldName;
	
	private int position;
	
	private String paramMode;

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getParamMode() {
		return paramMode;
	}

	public void setParamMode(String paramMode) {
		this.paramMode = paramMode;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	

}
