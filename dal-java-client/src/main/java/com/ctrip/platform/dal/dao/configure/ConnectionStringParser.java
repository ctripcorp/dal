package com.ctrip.platform.dal.dao.configure;

/**
 * 
 * @author gzxia
 *
 */
public interface ConnectionStringParser {

	public String decrypt(String dbname, String connStr);
}
