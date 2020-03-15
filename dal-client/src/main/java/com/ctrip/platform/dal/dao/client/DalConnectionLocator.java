package com.ctrip.platform.dal.dao.client;

import java.sql.Connection;
import java.util.Collection;

import com.ctrip.framework.dal.cluster.client.Cluster;
import com.ctrip.framework.dal.cluster.client.database.Database;
import com.ctrip.platform.dal.dao.configure.ClusterConfigProvider;
import com.ctrip.platform.dal.dao.configure.DalComponent;
import com.ctrip.platform.dal.dao.configure.DatabaseSet;
import com.ctrip.platform.dal.dao.datasource.ConnectionStringConfigureProvider;
import com.ctrip.platform.dal.dao.datasource.DataSourceIdentity;

public interface DalConnectionLocator extends DalComponent {
	
	void setup(Collection<DatabaseSet> databaseSets);
	
	Connection getConnection(String name) throws Exception;

	Connection getConnection(DataSourceIdentity id) throws Exception;

	ClusterConfigProvider getClusterConfigProvider();

	void setupCluster(Cluster cluster);

	void uninstallCluster(Cluster cluster);

}
