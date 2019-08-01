package com.ctrip.platform.dal.dao.client;

import java.sql.Connection;
import java.util.Set;

import com.ctrip.framework.dal.cluster.client.database.Database;
import com.ctrip.platform.dal.dao.configure.ClusterConfigProvider;
import com.ctrip.platform.dal.dao.configure.DalComponent;

public interface DalConnectionLocator extends DalComponent {
	
	void setup(Set<String> dbNames);
	
	Connection getConnection(String name) throws Exception;

	Connection getConnection(Database database) throws Exception;

	ClusterConfigProvider getClusterConfigProvider();

}
