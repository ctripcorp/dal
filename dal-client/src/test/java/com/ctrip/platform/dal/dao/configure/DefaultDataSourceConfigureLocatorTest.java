package com.ctrip.platform.dal.dao.configure;

import org.junit.Test;

import java.util.Properties;

import static com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants.*;
import static org.junit.Assert.*;

public class DefaultDataSourceConfigureLocatorTest {

    private static String connectionProperties1 = "connectTimeout=35000;socketTimeout=10000;statementInterceptors=com.mysql.jdbc.Customer,com.mysql.jdbc.DalDefaultStatementInterceptorV2";
    private static String connectionProperties2 = "connectTimeout=35000;socketTimeout=10000";
    private static String connectionProperties3 = "connectTimeout=35000;socketTimeout=10000;statementInterceptors=com.mysql.jdbc.DalDefaultStatementInterceptorV2,com.mysql.jdbc.Customer";
    private static String connectionProperties4 = "connectTimeout=35000;socketTimeout=10000;statementInterceptors=com.mysql.jdbc.Customer";
    private static String connectionProperties5 = "connectTimeout=35000;socketTimeout=10000;statementInterceptors=com.mysql.jdbc.DalDefaultStatementInterceptorV2";
    private static String connectionProperties6 = "connectTimeout=35000;socketTimeout=10000;statementInterceptors=com.mysql.jdbc.Customer,com.mysql.jdbc.Customer2,com.mysql.jdbc.DalDefaultStatementInterceptorV2";

    @Test
    public void addInterceptorsToConnectionProperties() {
        DefaultDataSourceConfigureLocator locator = new DefaultDataSourceConfigureLocator();
        Properties lowLevel = new Properties();
        lowLevel.setProperty(STATEMENT_INTERCEPTORS_KEY, DEFAULT_STATEMENT_INTERCEPTORS_VALUE);
        lowLevel.setProperty(CONNECTIONPROPERTIES, connectionProperties1);

        String interceptor = "com.mysql.jdbc.DalDefaultStatementInterceptorV2";

        //statementInterceptors has been set in connection properties and is last one
        locator.addInterceptorsToConnectionProperties(lowLevel, interceptor);
        assertEquals(connectionProperties1, lowLevel.getProperty(CONNECTIONPROPERTIES));

        //statementInterceptors has been set in connection properties and not the last one
        lowLevel.setProperty(CONNECTIONPROPERTIES, connectionProperties3);
        locator.addInterceptorsToConnectionProperties(lowLevel, interceptor);
        assertEquals(connectionProperties1, lowLevel.getProperty(CONNECTIONPROPERTIES));

        //no interceptor in dal.property
        interceptor = "";
        lowLevel.setProperty(CONNECTIONPROPERTIES, connectionProperties3);
        locator.addInterceptorsToConnectionProperties(lowLevel, interceptor);
        assertEquals(connectionProperties3, lowLevel.getProperty(CONNECTIONPROPERTIES));

        //interceptor set more than one in dal.property
        interceptor = "com.mysql.jdbc.Customer,com.mysql.jdbc.DalDefaultStatementInterceptorV2";
        lowLevel.setProperty(CONNECTIONPROPERTIES, connectionProperties2);
        locator.addInterceptorsToConnectionProperties(lowLevel, interceptor);
        assertEquals(connectionProperties1, lowLevel.getProperty(CONNECTIONPROPERTIES));

        //interceptor set more than one in dal.property and connection property also has
        interceptor = "com.mysql.jdbc.Customer2,com.mysql.jdbc.DalDefaultStatementInterceptorV2";
        lowLevel.setProperty(CONNECTIONPROPERTIES, connectionProperties4);
        locator.addInterceptorsToConnectionProperties(lowLevel, interceptor);
        assertEquals(connectionProperties6, lowLevel.getProperty(CONNECTIONPROPERTIES));

    }

}