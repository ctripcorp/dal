package com.ctrip.datasource.util;

import com.ctrip.datasource.util.entity.VariableConnectionStringInfo;
import org.junit.Assert;
import org.junit.Test;

public class VariableConnectionStringUtilsTest {

    @Test
    public void testGetConnectionStringFromAPI() throws Exception {
        String dbName = "qconfig";
        VariableConnectionStringInfo info = VariableConnectionStringUtils.getConnectionStringFromDBAPI(dbName, "FAT");
        Assert.assertNotNull(info);
    }
}
