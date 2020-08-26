package com.ctrip.platform.dal.daogen.enums;

public enum DatabaseType {
	
	MySQL("Arch.Data.MySqlProvider"),
	SQLServer("System.Sql.SqlClient");
	
	private String value;

	DatabaseType(String value){
		this.value = value;
	}

	public static DatabaseType getInstanceByValue(String value) {
		for (DatabaseType databaseType : DatabaseType.values()) {
			if (databaseType.getValue().equals(value)) {
				return  databaseType;
			}
		}
		return SQLServer;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
