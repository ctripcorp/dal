package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import com.ctrip.platform.dal.dao.helper.PoolPropertiesHelper;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

public class SingleDataSource implements DataSourceConfigureConstants {
    private static final Logger logger = LoggerFactory.getLogger(SingleDataSource.class);
    private PoolPropertiesHelper poolPropertiesHelper = PoolPropertiesHelper.getInstance();

    private String name;
    private DataSourceConfigure dataSourceConfigure;
    private DataSource dataSource;
    private Date enqueueTime;

    public String getName() {
        return name;
    }

    public DataSourceConfigure getDataSourceConfigure() {
        return dataSourceConfigure;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setEnqueueTime(Date enqueueTime) {
        this.enqueueTime = enqueueTime;
    }

    public Date getEnqueueTime() {
        return enqueueTime;
    }

    public SingleDataSource(String name, DataSourceConfigure dataSourceConfigure) throws SQLException {
        if (dataSourceConfigure == null)
            throw new SQLException("Can not find any connection configure for " + name);

        try {
            this.name = name;
            this.dataSourceConfigure = dataSourceConfigure;

            PoolProperties p = poolPropertiesHelper.convert(dataSourceConfigure);
            PoolPropertiesHolder.getInstance().setPoolProperties(p);
            org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource(p);
            this.dataSource = dataSource;

            dataSource.createPool();
            logger.info("Datasource[name=" + name + ", Driver=" + p.getDriverClassName() + "] created.");
        } catch (Throwable e) {
            logger.error(String.format("Error creating pool for data source %s", name), e);
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
            logger.error(e.getMessage(), e);
        } finally {
            if (con != null)
                try {
                    con.close();
                } catch (Throwable e) {
                    logger.error(e.getMessage(), e);
                }
        }
    }

}
