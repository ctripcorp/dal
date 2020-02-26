package com.ctrip.datasource.configure;

import com.ctrip.datasource.util.VariableConnectionStringUtils;
import com.ctrip.datasource.util.entity.VariableConnectionStringInfo;
import com.ctrip.platform.dal.dao.configure.DalConnectionStringConfigure;
import org.junit.Assert;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

public class VariableConnectionStringParserTest {
    private static final String DBNAME = "qconfig";
    //private static final String NOT_EXIST_DBNAME = "dalTest0000";
    private static final String TOKEN = "2olkdweut7sUbsrim-La";

    @Test
    public void testConnectionStringParser() throws Exception {
        String mgrUrl = "jdbc:mysql:replication://address=(type=slave)(protocol=tcp)(host=10.9.72.67)(port=55944),address=(type=master)(protocol=tcp)(host=10.25.82.137)(port=55944),address=(type=slave)(protocol=tcp)(host=10.60.53.211)(port=55944)/qconfig?useUnicode=true&characterEncoding=UTF-8";
        String url = "jdbc:mysql://qconfig.mysql.db.fat.qa.nt.ctripcorp.com:55111/qconfig?useUnicode=true&characterEncoding=UTF-8";
        VariableConnectionStringInfo info = VariableConnectionStringUtils.getConnectionStringFromDBAPI(DBNAME, "PRO");
        DalConnectionStringConfigure configure = VariableConnectionStringParser.parser(DBNAME, info, TOKEN);
        Assert.assertEquals(mgrUrl, configure.getConnectionUrl());
        VariableConnectionStringInfo info1 = VariableConnectionStringUtils.getConnectionStringFromDBAPI(DBNAME, "FAT");
        DalConnectionStringConfigure configure1 = VariableConnectionStringParser.parser(DBNAME, info1, TOKEN);
        Assert.assertEquals(url, configure1.getConnectionUrl());
    }
}
