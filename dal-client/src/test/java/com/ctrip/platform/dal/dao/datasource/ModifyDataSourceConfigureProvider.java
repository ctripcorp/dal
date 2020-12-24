package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.configure.IDataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.IDataSourceConfigureProvider;
import com.ctrip.platform.dal.dao.configure.MockDataSourceConfigure;

/**
 * Created by taochen on 2019/8/15.
 */
public class ModifyDataSourceConfigureProvider implements IDataSourceConfigureProvider {
    @Override
    public IDataSourceConfigure getDataSourceConfigure() {
        throw new RuntimeException();
    }

    @Override
    public IDataSourceConfigure forceLoadDataSourceConfigure() {
        String connectionUrl = "jdbc:mysql://1.1.1.1:3306/llj_test?useUnicode=true&characterEncoding=UTF-8";
        String userName = "root";
        String password = "!QAZ@WSX1qaz2wsx";
        String driverClass = "com.mysql.jdbc.Driver";
        String connectionProperties = "connectTimeout=1050";
        return new MockDataSourceConfigure(connectionUrl, userName, password, driverClass, connectionProperties);
    }
}
