package com.ctrip.platform.dal.dao.client;

import java.sql.Connection;
import java.util.Set;

import com.ctrip.platform.dal.dao.configure.DalComponent;

public interface DalConnectionLocator extends DalComponent {
	
	void setup(Set<String> dbNames);
	
	Connection getConnection(String name) throws Exception;
}
