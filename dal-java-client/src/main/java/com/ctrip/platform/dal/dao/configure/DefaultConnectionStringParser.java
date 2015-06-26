package com.ctrip.platform.dal.dao.configure;

public class DefaultConnectionStringParser implements ConnectionStringParser {

	@Override
	public String decrypt(String dbname, String connStr) {
		return connStr;
	}

}
