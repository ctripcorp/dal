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

    @Test
    public void addInterceptorsToConnectionProperties() {
        DefaultDataSourceConfigureLocator locator = new DefaultDataSourceConfigureLocator();
        Properties lowLevel = new Properties();
        lowLevel.setProperty(STATEMENT_INTERCEPTORS_KEY, DEFAULT_STATEMENT_INTERCEPTORS_VALUE);
        lowLevel.setProperty(CONNECTIONPROPERTIES, connectionProperties1);

        String interceptor = "com.mysql.jdbc.DalDefaultStatementInterceptorV2";

                //statementInterceptors has been set and is last one
        locator.addInterceptorsToConnectionProperties(lowLevel, interceptor);
        assertEquals(connectionProperties1, lowLevel.getProperty(CONNECTIONPROPERTIES));

        //statementInterceptors has been set and not the last one
        lowLevel.setProperty(CONNECTIONPROPERTIES, connectionProperties3);
        locator.addInterceptorsToConnectionProperties(lowLevel, interceptor);
        assertEquals(connectionProperties1, lowLevel.getProperty(CONNECTIONPROPERTIES));

        //no interceptor in properties
        interceptor = "";
        lowLevel.remove(STATEMENT_INTERCEPTORS_KEY);
        lowLevel.setProperty(CONNECTIONPROPERTIES, connectionProperties3);
        locator.addInterceptorsToConnectionProperties(lowLevel, interceptor);
        assertEquals(connectionProperties3, lowLevel.getProperty(CONNECTIONPROPERTIES));

        //customer interceptor in connectionProperty and not in properties
        lowLevel.remove(STATEMENT_INTERCEPTORS_KEY);
        lowLevel.setProperty(CONNECTIONPROPERTIES, connectionProperties4);
        locator.addInterceptorsToConnectionProperties(lowLevel, interceptor);
        assertEquals(connectionProperties4, lowLevel.getProperty(CONNECTIONPROPERTIES));

    }

}