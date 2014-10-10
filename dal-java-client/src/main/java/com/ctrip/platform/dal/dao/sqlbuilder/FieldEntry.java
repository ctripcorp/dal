package com.ctrip.platform.dal.dao.sqlbuilder;

class FieldEntry {

	private String fieldName;
	private Object paramValue;
	private int sqlType;

	public FieldEntry(String fieldName, Object paramValue, int sqlType) {
		super();
		this.fieldName = fieldName;
		this.paramValue = paramValue;
		this.sqlType = sqlType;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public Object getParamValue() {
		return paramValue;
	}

	public void setParamValue(Object paramValue) {
		this.paramValue = paramValue;
	}

	public int getSqlType() {
		return sqlType;
	}

	public void setSqlType(int sqlType) {
		this.sqlType = sqlType;
	}

}