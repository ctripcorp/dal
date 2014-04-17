package com.ctrip.platform.dal.datasource;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.jdbc.pool.PoolProperties;

public class LocalDataSourceProvider extends
		ConcurrentHashMap<String, DataSource> {
	private static final long serialVersionUID = -5752249323568785554L;
	private static final Log log = LogFactory
			.getLog(LocalDataSourceProvider.class);
	private static Object _lockObj = new Object();

	Map<String, String[]> props = new HashMap<String, String[]>();
	
	public void initialize(String configFile){
		AllInOneConfigParser.newInstance().initialize(configFile);
		props = AllInOneConfigParser.newInstance()
		.getDBAllInOneConfig();
	}

	public Set<String> keySet() {
		synchronized (_lockObj) {
			return this.props.keySet();
		}
	}

	public boolean refresh(String config) {
		String key = null;
		if ((key = AllInOneConfigParser.newInstance().refresh(config)) != null) {
			synchronized (_lockObj) {
				props = AllInOneConfigParser.newInstance()
						.getDBAllInOneConfig();
			}
			DataSource ds = this.get(key);
			if (ds == null) {
				synchronized (_lockObj) {
					if (AllInOneConfigParser.newInstance().remove(key)) {
						props = AllInOneConfigParser.newInstance()
								.getDBAllInOneConfig();
					}
				}
			}
			return ds != null;
		}

		return false;
	}

	public DataSource get(Object name) {
		DataSource ds = (DataSource) super.get(name);
		if (ds == null) {
			try {
				ds = createDataSource(name);
				DataSource d = (DataSource) putIfAbsent((String) name, ds);
				if (d != null) {
					ds = d;
				}
			} catch (SQLException e) {
				log.error("Creating DataSource error:" + e.getMessage(), e);
			}
		}
		return ds;
	}

	private javax.sql.DataSource createDataSource(Object name)
			throws SQLException {
		PoolProperties p = new PoolProperties();

		String[] prop = (String[]) this.props.get(name);
		p.setUrl(prop[0]);
		p.setUsername(prop[1]);
		p.setPassword(prop[2]);
		p.setDriverClassName(prop[3]);
		p.setJmxEnabled(true);
		p.setTestWhileIdle(false);
		p.setTestOnBorrow(true);
		p.setValidationQuery("SELECT 1");
		p.setTestOnReturn(false);
		p.setValidationInterval(30000L);
		p.setTimeBetweenEvictionRunsMillis(30000);
		p.setMaxActive(100);
		p.setInitialSize(10);
		p.setMaxWait(10000);
		p.setRemoveAbandonedTimeout(60);
		p.setMinEvictableIdleTimeMillis(30000);
		p.setMinIdle(10);
		p.setLogAbandoned(true);
		p.setRemoveAbandoned(true);
		p.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");

		org.apache.tomcat.jdbc.pool.DataSource ds = new org.apache.tomcat.jdbc.pool.DataSource(
				p);

		ds.createPool();
		return ds;
	}
}
