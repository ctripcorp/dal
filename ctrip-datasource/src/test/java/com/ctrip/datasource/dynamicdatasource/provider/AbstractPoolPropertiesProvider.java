package com.ctrip.datasource.dynamicdatasource.provider;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants;
import com.ctrip.platform.dal.dao.datasource.PoolPropertiesChanged;
import com.ctrip.platform.dal.dao.datasource.PoolPropertiesProvider;

import java.util.HashMap;
import java.util.Map;

public class AbstractPoolPropertiesProvider implements PoolPropertiesProvider, DataSourceConfigureConstants {
    @Override
    public Map<String, String> getPoolProperties() {
        return getMap();
    }

    protected Map<String, String> getMap() {
        Map<String, String> map = new HashMap<>();
        map.put(TESTWHILEIDLE, "false");
        map.put(TESTONBORROW, "true");
        map.put(TESTONRETURN, "false");
        map.put(VALIDATIONQUERY, "SELECT 1");
        map.put(VALIDATIONINTERVAL, "30000");
        map.put(VALIDATORCLASSNAME, "com.ctrip.platform.dal.dao.datasource.DataSourceValidator");
        map.put(TIMEBETWEENEVICTIONRUNSMILLIS, "5000");
        map.put(MAXACTIVE, "100");
        map.put(MINIDLE, "0");
        map.put(MAXWAIT, "10000");
        map.put(MAX_AGE, "28000000");
        map.put(INITIALSIZE, "1");
        map.put(REMOVEABANDONEDTIMEOUT, "65");
        map.put(REMOVEABANDONED, "true");
        map.put(LOGABANDONED, "false");
        map.put(MINEVICTABLEIDLETIMEMILLIS, "30000");
        map.put(CONNECTIONPROPERTIES,
                "sendTimeAsDateTime=false;sendStringParametersAsUnicode=false;rewriteBatchedStatements=true;allowMultiQueries=true;useUnicode=true;characterEncoding=UTF-8;useSSL=false");
        map.put(JDBC_INTERCEPTORS,
                "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer;com.ctrip.datasource.interceptor.CtripConnectionState");
        return map;
    }

    @Override
    public void addPoolPropertiesChangedListener(PoolPropertiesChanged callback) {}

}
