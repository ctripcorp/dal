package com.ctrip.datasource.configure;

import com.ctrip.framework.clogging.agent.MessageManager;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class DataSourceConfigureProcessorTest {
    @Test
    public void testGetDataSourceConfigure() {
        // emulate datasource.xml
        DataSourceConfigure config = new DataSourceConfigure();
        config.setName("SimpleShard_0");
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
        map.put("maxWait", "500");
        // map.put("maxAge", "30000");
        map.put("initialSize", "3");
        map.put("removeAbandonedTimeout", "60");
        map.put("removeAbandoned", "true");
        map.put("logAbandoned", "true");
        map.put("minEvictableIdleTimeMillis", "30000");
        map.put("connectionProperties", "rewriteBatchedStatements=true;allowMultiQueries=true");
        config = DataSourceConfigureProcessor.getInstance().getDataSourceConfigure(config);
        MessageManager.getInstance().shutdown();
    }
}
