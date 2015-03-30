package com.ctrip.platform.dal.dao.client;

import java.sql.Connection;
import java.util.Map;
import java.util.Set;

public interface DalConnectionLocator {
	void initLocator(Map<String, String> settings);
	
	Set<String> getDBNames();
	
	Connection getConnection(String name) throws Exception;
}
