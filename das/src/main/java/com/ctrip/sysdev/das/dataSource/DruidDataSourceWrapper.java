package com.ctrip.sysdev.das.dataSource;

import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSource;
import com.ctrip.sysdev.das.commons.DataSourceWrapper;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class DruidDataSourceWrapper implements DataSourceWrapper {
	private DruidDataSource dataSource;

	private static final Logger logger = LoggerFactory
			.getLogger(DruidDataSourceWrapper.class);

	@Inject
	public DruidDataSourceWrapper(
			@Named("driverClass") String driverClass,
			@Named("jdbcUrl") String jdbcUrl,
			@Named("user") String user,
			@Named("password") String password,
			@Named("initialSize") int initialSize,
			@Named("maxActive") int maxActive,
			@Named("minIdle") int minIdle,
			@Named("maxWait") int maxWait,
			@Named("timeBetweenEvictionRunsMillis") int timeBetweenEvictionRunsMillis,
			@Named("minEvictableIdleTimeMillis") int minEvictableIdleTimeMillis,
			@Named("validationQuery") String validationQuery)
			throws SQLException {
		dataSource = new DruidDataSource();
		dataSource.setDriverClassName(driverClass);
		dataSource.setUrl(jdbcUrl);
		dataSource.setUsername(user);
		dataSource.setPassword(password);

		dataSource.setInitialSize(initialSize);
		dataSource.setMinIdle(minIdle);
		dataSource.setMaxActive(maxActive);
		dataSource.setMaxWait(maxWait);

		dataSource
				.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
		dataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
		dataSource.setValidationQuery(validationQuery);
		dataSource.setTestWhileIdle(true);
		dataSource.setTestOnBorrow(false);
		dataSource.setTestOnReturn(false);
		dataSource.init();
		try {
			Connection connection = dataSource.getConnection();
			connection.close();
			logger.info("数据连接池已经建立:jdbcUrl=" + jdbcUrl);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	@Override
	public void init() throws SQLException {
		dataSource.init();

	}

	@Override
	public void close() {
		dataSource.close();
	}

}
