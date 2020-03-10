package com.ctrip.datasource.dynamicdatasource;

import com.ctrip.datasource.configure.DalDataSourceFactory;
import com.ctrip.datasource.configure.MockConnectionStringConfigureProvider;
import com.ctrip.platform.dal.dao.datasource.RefreshableDataSource;
import org.junit.Assert;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.DatabaseMetaData;

public class MGRDataSourceSwitchTest {

    @Test
    public void testExecuteListenerSwitchDataSource() throws Exception {
        String mgrUrl1 = "jdbc:mysql://address=(type=master)(protocol=tcp)(host=10.2.7.196)(port=3306):3306:3306/";
        String mgrUrl2 = "jdbc:mysql://address=((type=master)(protocol=tcp)(host=10.2.7.187)(port=3306):3306:3306/";
        String mgrUrl3 = "jdbc:mysql://address=((type=master)(protocol=tcp)(host=10.2.7.184)(port=3306):3306:3306/";
        String normalUrl1 = "jdbc:mysql://localhost:3306/test";

        MockConnectionStringConfigureProvider provider = new MockConnectionStringConfigureProvider();
        DalDataSourceFactory factory = new DalDataSourceFactory();
        DataSource ds1 = factory.createVariableTypeDataSource(provider);

        DatabaseMetaData metaData1 = ds1.getConnection().getMetaData();
        String url1 = metaData1.getURL();
        Assert.assertTrue(mgrUrl1.equalsIgnoreCase(url1) || mgrUrl2.equalsIgnoreCase(url1) || mgrUrl3.equalsIgnoreCase(url1));

        provider.switchDataSource();

        Thread.sleep(2000);

        DatabaseMetaData metaData2 = ds1.getConnection().getMetaData();
        String url2 = metaData2.getURL();
        Assert.assertTrue(normalUrl1.equalsIgnoreCase(url2));
    }

    @Test
    public void testDynamicSwitchDataSource() throws Exception {
        String mgrJdbcUrl1 = "jdbc:mysql:replication://address=(type=master)(protocol=tcp)(host=10.2.7.196)(port=3306),address=((type=master)(protocol=tcp)(host=10.2.7.184)(port=3306)/kevin";
        String mgrJdbcUrl2 = "jdbc:mysql:replication://address=(type=master)(protocol=tcp)(host=10.2.7.196)(port=3306),address=((type=master)(protocol=tcp)(host=10.2.7.187)(port=3306)/kevin";
        //String mgrJdbcUrl3 = "jdbc:mysql:replication://address=(type=master)(protocol=tcp)(host=10.2.7.184)(port=3306),address=((type=master)(protocol=tcp)(host=10.2.7.187)(port=3306)/kevin";

        String mgrUrl1 = "jdbc:mysql://address=(type=master)(protocol=tcp)(host=10.2.7.196)(port=3306):3306:3306/";
        String mgrUrl2 = "jdbc:mysql://address=((type=master)(protocol=tcp)(host=10.2.7.187)(port=3306):3306:3306/";
        String mgrUrl3 = "jdbc:mysql://address=((type=master)(protocol=tcp)(host=10.2.7.184)(port=3306):3306:3306/";

        DynamicConnectionStringConfigureProvider provider = new DynamicConnectionStringConfigureProvider("unused");
        DalDataSourceFactory factory = new DalDataSourceFactory();
        DataSource ds1 = factory.createVariableTypeDataSource(provider);
        RefreshableDataSource refreshableDataSource = (RefreshableDataSource) ds1;

        provider.setUrl(mgrJdbcUrl1);
        Thread.sleep(6000);

        String testUrl1 = ((org.apache.tomcat.jdbc.pool.DataSource) refreshableDataSource.getSingleDataSource().getDataSource()).getUrl();
        Assert.assertEquals(mgrJdbcUrl1, testUrl1);
        DatabaseMetaData metaData1 = ds1.getConnection().getMetaData();
        for (int i = 0; i < 3; ++i) {
            String url1 = metaData1.getURL();
            Assert.assertTrue(mgrUrl1.equalsIgnoreCase(url1) || mgrUrl2.equalsIgnoreCase(url1));
        }

        provider.setUrl(mgrJdbcUrl2);
        Thread.sleep(6000);

        Assert.assertEquals(mgrJdbcUrl2, ((org.apache.tomcat.jdbc.pool.DataSource) refreshableDataSource.getSingleDataSource().getDataSource()).getUrl());
        DatabaseMetaData metaData2 = ds1.getConnection().getMetaData();
        for (int i = 0; i < 3; ++i) {
            String url2 = metaData2.getURL();
            Assert.assertTrue(mgrUrl1.equalsIgnoreCase(url2) || mgrUrl3.equalsIgnoreCase(url2));
        }
    }
}
