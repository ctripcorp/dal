package com.ctrip.platform.dal.dao.sqlbuilder;


class FieldEntry {

	private String fieldName;
	private Object paramValue;
	private int sqlType;
	private boolean sensitive = false;
	private boolean inParam = false;

	public FieldEntry(String fieldName, Object paramValue, int sqlType) {
		this.fieldName = fieldName;
		this.paramValue = paramValue;
		this.sqlType = sqlType;
	}
	
	public FieldEntry(String fieldName, Object paramValue, int sqlType,
			boolean sensitive) {
		this(fieldName, paramValue, sqlType);
		this.sensitive = sensitive;
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

	public boolean isSensitive() {
		return sensitive;
	}

	public void setSensitive(boolean sensitive) {
		this.sensitive = sensitive;
	}

	public boolean isInParam() {
		return inParam;
	}

	public FieldEntry setInParam(boolean inParam) {
		this.inParam = inParam;
		return this;
	}
}