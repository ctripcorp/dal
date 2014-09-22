package com.ctrip.platform.dal.daogen.enums;

public enum DatabaseType {
	
	MySQL("Arch.Data.MySqlProvider"),
	SQLServer("System.Sql.SqlClient");
	
	private String value;
	
	DatabaseType(String value){
		this.value = value;
	}
	
	public String getValue(){
		return this.value;
	}

}
