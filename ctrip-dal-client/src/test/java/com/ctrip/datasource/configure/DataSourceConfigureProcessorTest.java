package com.ctrip.datasource.configure;

import com.ctrip.platform.dal.dao.configure.DatabasePoolConfig;
import org.junit.Test;

public class DataSourceConfigureProcessorTest {
    @Test
    public void testGetDatabasePoolConfig() {
        DatabasePoolConfig config = new DatabasePoolConfig();
        config.setName("test");
        config = DataSourceConfigureProcessor.getDatabasePoolConfig(config);
    }
}
