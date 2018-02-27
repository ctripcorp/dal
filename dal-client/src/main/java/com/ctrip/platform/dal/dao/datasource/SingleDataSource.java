package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import com.ctrip.platform.dal.dao.datasource.tomcat.DalTomcatDataSource;
import com.ctrip.platform.dal.dao.helper.PoolPropertiesHelper;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class SingleDataSource implements DataSourceConfigureConstants {
    private static final Logger LOGGER = LoggerFactory.getLogger(SingleDataSource.class);
    private PoolPropertiesHelper poolPropertiesHelper = PoolPropertiesHelper.getInstance();

    private String name;
    private DataSourceConfigure dataSourceConfigure;
    private DataSource dataSource;

    public String getName() {
        return name;
    }

    public DataSourceConfigure getDataSourceConfigure() {
        return dataSourceConfigure;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public SingleDataSource(String name, DataSourceConfigure dataSourceConfigure) throws SQLException {
        if (dataSourceConfigure == null)
            throw new SQLException("Can not find any connection configure for " + name);

        try {
            this.name = name;
            this.dataSourceConfigure = dataSourceConfigure;

            PoolProperties p = poolPropertiesHelper.convert(dataSourceConfigure);
            PoolPropertiesHolder.getInstance().setPoolProperties(p);
            org.apache.tomcat.jdbc.pool.DataSource dataSource = new DalTomcatDataSource(p);
            this.dataSource = dataSource;

            dataSource.createPool();
            LOGGER.info("Datasource[name=" + name + ", Driver=" + p.getDriverClassName() + "] created.");
        } catch (Throwable e) {
            LOGGER.error(String.format("Error creating pool for data source %s", name), e);
            // throw e;
        }
    }

    private void testConnection(org.apache.tomcat.jdbc.pool.DataSource dataSource) throws SQLException {
        if (dataSource == null)
            return;

        Connection con = null;
        try {
            con = dataSource.getConnection();
        } catch (Throwable e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            if (con != null)
                try {
                    con.close();
                } catch (Throwable e) {
                    LOGGER.error(e.getMessage(), e);
                }
        }
    }

}
