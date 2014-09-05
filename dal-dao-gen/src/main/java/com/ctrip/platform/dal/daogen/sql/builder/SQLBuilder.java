package com.ctrip.platform.dal.daogen.sql.builder;

public class SQLBuilder {
	public static String net2Java(String sql){
		return sql.replaceAll("@\\w+", "?");
	}
}
