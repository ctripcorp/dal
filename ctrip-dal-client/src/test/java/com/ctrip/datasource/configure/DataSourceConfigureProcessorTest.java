package com.ctrip.datasource.configure;

import com.ctrip.platform.dal.dao.configure.DatabasePoolConfig;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class DataSourceConfigureProcessorTest {
    @Test
    public void testGetDatabasePoolConfig() {
        //emulate datasource.xml
        DatabasePoolConfig config = new DatabasePoolConfig();
        config.setName("test");
        Map<String, String> map = new HashMap<>();
        config.setMap(map);
        map.put("testWhileIdle", "false");
        map.put("testOnBorrow", "false");
        map.put("testOnReturn", "false");
        map.put("validationQuery", "SELECT 1");
        map.put("validationInterval", "30000");
        map.put("timeBetweenEvictionRunsMillis", "5000");
        map.put("maxActive", "100");
        map.put("minIdle", "0");
        map.put("maxWait", "10000");
        //map.put("maxAge", "30000");
        map.put("initialSize", "3");
        map.put("removeAbandonedTimeout", "60");
        map.put("removeAbandoned", "true");
        map.put("logAbandoned", "true");
        map.put("minEvictableIdleTimeMillis", "30000");
        map.put("connectionProperties", "rewriteBatchedStatements=true;allowMultiQueries=true");
        config = DataSourceConfigureProcessor.getDatabasePoolConfig(config);
    }
}
