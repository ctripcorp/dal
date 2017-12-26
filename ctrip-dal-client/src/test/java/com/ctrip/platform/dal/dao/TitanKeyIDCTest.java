package com.ctrip.platform.dal.dao;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.ctrip.datasource.configure.ConnectionStringProvider;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ctrip.datasource.titan.TitanProvider;

public class TitanKeyIDCTest {
    private static final String databaseName = "mysqldaltest01db_W";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        DalClientFactory.initClientFactory();
        DalClientFactory.warmUpConnections();
    }

    @Test
    public void testTitanKeyIDC() throws Exception {
        TitanProvider provider = new TitanProvider();
        Map<String, String> settings = new HashMap<>();
        settings.put(ConnectionStringProvider.USE_LOCAL_CONFIG, "false");
        settings.put(ConnectionStringProvider.DATABASE_CONFIG_LOCATION, "$classpath");
        provider.initialize(settings);
        Set<String> names = new HashSet<>();
        names.add("mysqldaltest01db_R_SH");
        provider.setup(names);

    }

}
