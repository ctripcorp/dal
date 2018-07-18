package com.ctrip.platform.dal.daogen.domain;


public class StoredProcedure implements Comparable<StoredProcedure> {
	
	private String schema;
	
	private String name;

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public int hashCode() {
		return this.getName().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StoredProcedure) {
			StoredProcedure castedObj = (StoredProcedure) obj;
			return castedObj.getName().equals(this.getName());
		} else if (obj instanceof String) {
			String castedObj = (String) obj;
			return castedObj.equals(this.getName());
		}

		return false;
	}

	@Override
	public int compareTo(StoredProcedure o) {
		
		int schemaCompare = this.getSchema().toLowerCase().compareTo(o.getSchema().toLowerCase());
		
		if(0 != schemaCompare){
			return schemaCompare;
		}
		
		return this.getName().toLowerCase().compareTo(o.getName().toLowerCase());
	}

}
