package com.ctrip.platform.dal.dao.client;

import java.sql.Connection;
import java.util.Collection;

import com.ctrip.platform.dal.cluster.Cluster;
import com.ctrip.platform.dal.dao.configure.DalComponent;
import com.ctrip.platform.dal.dao.configure.DatabaseSet;
import com.ctrip.platform.dal.dao.configure.IntegratedConfigProvider;
import com.ctrip.platform.dal.dao.datasource.DataSourceIdentity;

public interface DalConnectionLocator extends DalComponent {
	
	void setup(Collection<DatabaseSet> databaseSets);
	
	Connection getConnection(String name) throws Exception;

	Connection getConnection(DataSourceIdentity id) throws Exception;

	IntegratedConfigProvider getIntegratedConfigProvider();

	void setupCluster(Cluster cluster);

	void uninstallCluster(Cluster cluster);

}
