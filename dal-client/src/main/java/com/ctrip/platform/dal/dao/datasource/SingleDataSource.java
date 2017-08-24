package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;

import javax.sql.DataSource;
import java.util.Date;

public class SingleDataSource {
    private String name;
    private DataSourceConfigure dataSourceConfigure;
    private DataSource dataSource;
    private Date enqueueTime;

    public SingleDataSource(String name, DataSourceConfigure dataSourceConfigure, DataSource dataSource,
            Date enqueueTime) {
        this.name = name;
        this.dataSourceConfigure = dataSourceConfigure;
        this.dataSource = dataSource;
        this.enqueueTime = enqueueTime;
    }

    public String getName() {
        return name;
    }

    public DataSourceConfigure getDataSourceConfigure() {
        return dataSourceConfigure;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public Date getEnqueueTime() {
        return enqueueTime;
    }

}
