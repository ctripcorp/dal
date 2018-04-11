package com.ctrip.datasource.dynamicdatasource.provider;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import com.ctrip.platform.dal.dao.configure.PoolPropertiesConfigure;
import com.ctrip.platform.dal.dao.datasource.PoolPropertiesChanged;
import com.ctrip.platform.dal.dao.datasource.PoolPropertiesProvider;

import java.util.Properties;

public class AbstractPoolPropertiesProvider implements PoolPropertiesProvider, DataSourceConfigureConstants {
    @Override
    public PoolPropertiesConfigure getPoolProperties() {
        Properties p = getProperties();
        DataSourceConfigure configure = new DataSourceConfigure("", p);
        return configure;
    }

    protected Properties getProperties() {
        Properties p = new Properties();
        p.setProperty(TESTWHILEIDLE, "false");
        p.setProperty(TESTONBORROW, "true");
        p.setProperty(TESTONRETURN, "false");
        p.setProperty(VALIDATIONQUERY, "SELECT 1");
        p.setProperty(VALIDATIONINTERVAL, "30000");
        p.setProperty(VALIDATORCLASSNAME, "com.ctrip.platform.dal.dao.datasource.DataSourceValidator");
        p.setProperty(TIMEBETWEENEVICTIONRUNSMILLIS, "5000");
        p.setProperty(MAXACTIVE, "100");
        p.setProperty(MINIDLE, "0");
        p.setProperty(MAXWAIT, "10000");
        p.setProperty(MAX_AGE, "28000000");
        p.setProperty(INITIALSIZE, "1");
        p.setProperty(REMOVEABANDONEDTIMEOUT, "65");
        p.setProperty(REMOVEABANDONED, "true");
        p.setProperty(LOGABANDONED, "false");
        p.setProperty(MINEVICTABLEIDLETIMEMILLIS, "30000");
        p.setProperty(CONNECTIONPROPERTIES,
                "sendTimeAsDateTime=false;sendStringParametersAsUnicode=false;rewriteBatchedStatements=true;allowMultiQueries=true;useUnicode=true;characterEncoding=UTF-8;useSSL=false");
        p.setProperty(JDBC_INTERCEPTORS,
                "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
        return p;
    }

    @Override
    public void addPoolPropertiesChangedListener(PoolPropertiesChanged callback) {}

}
