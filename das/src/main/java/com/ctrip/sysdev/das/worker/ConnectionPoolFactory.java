package com.ctrip.sysdev.das.worker;

import com.alibaba.druid.pool.DruidDataSource;

public class ConnectionPoolFactory {
	private String   jdbcUrl;
	private String   user;
	private String   password;
	private String   driverClass;
	private int      initialSize                = 10;
	private int      minIdle                    = 3;
	private int      maxIdle                    = 8;
	private int      maxActive                  = 8;
	private String   validationQuery            = "SELECT 1";
	private boolean  testOnBorrow               = false;
    private long     minEvictableIdleTimeMillis = 3000;
    
	public DruidDataSource createPool() {
		DruidDataSource dataSource = new DruidDataSource();

        dataSource.setInitialSize(initialSize);
        dataSource.setMaxActive(maxActive);
        dataSource.setMinIdle(minIdle);
        dataSource.setMaxIdle(maxIdle);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(jdbcUrl);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setValidationQuery(validationQuery);
        dataSource.setTestOnBorrow(testOnBorrow);
        dataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        
        return dataSource;
	}
}
