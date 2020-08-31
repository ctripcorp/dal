package com.ctrip.datasource.configure;

import com.ctrip.platform.dal.dao.configure.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

/**
 * @author c7ch23en
 */
public class CtripLocalConnectionStringProviderTest {

    @Test
    public void testGetConnectionStrings() throws Exception {
        CtripLocalContext context = new CtripLocalContextImpl(null,
                null, true, mockDatabaseSets());
        CtripLocalConnectionStringProvider provider = new CtripLocalConnectionStringProvider(context);
        Set<String> names = new HashSet<>();
        names.add("mock_db11");
        names.add("mock_db21");
        names.add("mock_db22");
        names.add("mock_db31");
        names.add("mock_db32");
        names.add("mock_db33");
        names.add("mock_db34");
        Map<String, DalConnectionString> connStrings = provider.getConnectionStrings(names);
        DalConnectionString connString = connStrings.get("mock_db11");
        assertConnString(connString, true,
                "dst56614", 4444, "mock_db11_db", "root", "");
        connString = connStrings.get("mock_db21");
        assertConnString(connString, true,
                CtripLocalMySQLPropertiesParser.DEFAULT_HOST, CtripLocalMySQLPropertiesParser.DEFAULT_PORT,
                "mock_db21",
                CtripLocalMySQLPropertiesParser.DEFAULT_UID, CtripLocalMySQLPropertiesParser.DEFAULT_PWD);
        connString = connStrings.get("mock_db22");
        assertConnString(connString, true,
                "127.0.0.127", CtripLocalMySQLPropertiesParser.DEFAULT_PORT,
                "mockdb22db",
                CtripLocalMySQLPropertiesParser.DEFAULT_UID, CtripLocalMySQLPropertiesParser.DEFAULT_PWD);
        connString = connStrings.get("mock_db31");
        assertConnString(connString, false,
                "mockset3db.cn1.global.ctrip.com", 3001, "mockset3db", "sa", "sa");
        connString = connStrings.get("mock_db32");
        assertConnString(connString, false,
                CtripLocalSQLServerPropertiesParser.DEFAULT_HOST, CtripLocalSQLServerPropertiesParser.DEFAULT_PORT,
                "mockset3db",
                CtripLocalSQLServerPropertiesParser.DEFAULT_UID, CtripLocalSQLServerPropertiesParser.DEFAULT_PWD);
        connString = connStrings.get("mock_db33");
        assertConnString(connString, false,
                CtripLocalSQLServerPropertiesParser.DEFAULT_HOST, 3003,
                "mockset3db",
                CtripLocalSQLServerPropertiesParser.DEFAULT_UID, CtripLocalSQLServerPropertiesParser.DEFAULT_PWD);
        connString = connStrings.get("mock_db34");
        assertConnString(connString, false,
                CtripLocalSQLServerPropertiesParser.DEFAULT_HOST, 3004,
                "mockset3db",
                CtripLocalSQLServerPropertiesParser.DEFAULT_UID, CtripLocalSQLServerPropertiesParser.DEFAULT_PWD);
    }

    private DatabaseSets mockDatabaseSets() {
        List<DatabaseSet> databaseSets = new ArrayList<>();
        // only database.config
        databaseSets.add(mockDatabaseSet("mockSet1",
                "mock_db11"));
        // 1. none; 2. local-databases.properties
        databaseSets.add(mockDatabaseSet("mockSet2",
                "mock_db21", "mock_db22"));
        // databaseSet (local-databases.properties) +
        // 1. database.config; 2. none; 3. local-databases.properties; 4. local-databases.properties + database.config
        databaseSets.add(mockDatabaseSet("mockSet3",
                "mock_db31", "mock_db32", "mock_db33", "mock_db34"));
        return new DatabaseSetsImpl(databaseSets);
    }

    private DatabaseSet mockDatabaseSet(String logicName, String... dbNames) {
        try {
            return new DefaultDatabaseSet(logicName, "mySqlProvider", mockDatabases(dbNames));
        } catch (Exception e) {
            throw new RuntimeException("mockDatabaseSet errored", e);
        }
    }

    private Map<String, DataBase> mockDatabases(String... dbNames) {
        Map<String, DataBase> databases = new HashMap<>();
        if (dbNames.length == 1) {
            String dbName = dbNames[0];
            databases.put(dbName, new DefaultDataBase(dbName, true, "", dbName));
        } else {
            for (int i = 0; i < dbNames.length; i++) {
                String dbName = dbNames[i];
                databases.put(dbName, new DefaultDataBase(dbName, true, i + "", dbName));
            }
        }
        return databases;
    }

    private void assertConnString(DalConnectionString connString,
                                  boolean isMySql, String host, int port, String dbName,
                                  String uid, String pwd) {
        Assert.assertTrue(connString instanceof DalLocalConnectionString);
        String url;
        if (isMySql)
            url = String.format(ConnectionStringParser.DBURL_MYSQL, host, port, dbName,
                    ConnectionStringParser.DEFAULT_ENCODING);
        else
            url = String.format(ConnectionStringParser.DBURL_SQLSERVER, host, port, dbName);
        Assert.assertEquals(url, connString.getIPConnectionStringConfigure().getConnectionUrl());
        Assert.assertEquals(uid, connString.getIPConnectionStringConfigure().getUserName());
        Assert.assertEquals(pwd, connString.getIPConnectionStringConfigure().getPassword());
    }

}
