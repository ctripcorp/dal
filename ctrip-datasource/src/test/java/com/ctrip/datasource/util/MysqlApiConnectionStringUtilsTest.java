package com.ctrip.datasource.util;

import com.ctrip.datasource.util.entity.MysqlApiConnectionStringInfo;
import org.junit.Assert;
import org.junit.Test;

public class MysqlApiConnectionStringUtilsTest {

    @Test
    public void testGetConnectionStringFromAPI() throws Exception {
        String dbName = "qconfig";
        MysqlApiConnectionStringInfo info = MysqlApiConnectionStringUtils.getConnectionStringFromDBAPI(dbName, "FAT");
        Assert.assertNotNull(info);
    }
}
