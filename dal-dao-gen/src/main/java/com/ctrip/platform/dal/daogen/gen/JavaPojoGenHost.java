package com.ctrip.platform.dal.daogen.gen;

import java.util.List;

import com.ctrip.platform.dal.daogen.pojo.FieldMeta;

public class JavaPojoGenHost {
	
	private String daoNamespace;
	
	private String className;
	
	private List<FieldMeta> fields;

	public String getDaoNamespace() {
		return daoNamespace;
	}

	public void setDaoNamespace(String daoNamespace) {
		this.daoNamespace = daoNamespace;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public List<FieldMeta> getFields() {
		return fields;
	}

	public void setFields(List<FieldMeta> fields) {
		this.fields = fields;
	}

}
