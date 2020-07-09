package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.configure.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

import static com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants.*;
import static org.junit.Assert.*;

public class ForceSwitchableDataSourceTest {
    private final static String IPHOST = "10.32.20.128";
    private final static String DOMAINHOST = "dst56614";
    private final static String INVALIDHOST = "1.1.1.1";

    @Before
    public void beforeTest() {
        DataSourceCreator.getInstance().closeAllDataSources();
    }

    @Test
    public void testRestore() throws Exception {
        IDataSourceConfigureProvider provider = new MockDataSourceConfigureProvider();
        ForceSwitchableDataSource dataSource = new ForceSwitchableDataSource(provider);
        MockSwitchListener listener = new MockSwitchListener();
        dataSource.addListener(listener);

        SwitchableDataSourceStatus status1 = dataSource.restore();
        assertFalse(status1.isForceSwitched());
        assertEquals(IPHOST, status1.getHostName().toLowerCase());
        assertEquals("3306", status1.getPort().toString());
        assertTrue(status1.isPoolCreated());

        SwitchableDataSourceStatus status2 = dataSource.getStatus();
        assertFalse(status2.isForceSwitched());
        assertEquals(IPHOST, status2.getHostName().toLowerCase());
        assertEquals("3306", status2.getPort().toString());
        assertTrue(status2.isPoolCreated());
    }

    @Test
    public void testSwitchToValidHost() throws Exception {

        IDataSourceConfigureProvider provider = new MockDataSourceConfigureProvider();
        ForceSwitchableDataSource dataSource = new ForceSwitchableDataSource(provider);
        MockSwitchListener listener = new MockSwitchListener();
        dataSource.addListener(listener);

//        getStatus
        SwitchableDataSourceStatus status0 = dataSource.getStatus();
        assertFalse(status0.isForceSwitched());
        assertEquals(IPHOST, status0.getHostName().toLowerCase());
        assertEquals("3306", status0.getPort().toString());
        assertTrue(status0.isPoolCreated());

//        first forceSwitch
        DataSourceConfigure dataSourceConfigure1 = new DataSourceConfigure();
        SwitchableDataSourceStatus status1 = dataSource.forceSwitch(SerializableDataSourceConfig.valueOf(dataSourceConfigure1), DOMAINHOST, 3306);
        Thread.sleep(2000);
        assertEquals("onForceSwitchSuccess", listener.getOnCallMethodName());
        assertFalse(status1.isForceSwitched());
        assertEquals(IPHOST, status1.getHostName().toLowerCase());
        assertEquals("3306", status1.getPort().toString());
        assertTrue(status1.isPoolCreated());

//        getStatus
        SwitchableDataSourceStatus status2 = dataSource.getStatus();
        assertTrue(status2.isForceSwitched());
        assertEquals(DOMAINHOST, status2.getHostName().toLowerCase());
        assertEquals("3306", status2.getPort().toString());
        assertTrue(status2.isPoolCreated());

//        second forceSwitch
        DataSourceConfigure dataSourceConfigure3 = new DataSourceConfigure();
        SwitchableDataSourceStatus status3 = dataSource.forceSwitch(SerializableDataSourceConfig.valueOf(dataSourceConfigure3), IPHOST, 3306);
        Thread.sleep(2000);
        assertEquals("onForceSwitchSuccess", listener.getOnCallMethodName());
        assertTrue(status3.isForceSwitched());
        assertEquals(DOMAINHOST, status3.getHostName().toLowerCase());
        assertEquals("3306", status3.getPort().toString());
        assertTrue(status3.isPoolCreated());

//        getStatus
        SwitchableDataSourceStatus status4 = dataSource.getStatus();
        assertTrue(status4.isForceSwitched());
        assertEquals(IPHOST, status4.getHostName().toLowerCase());
        assertEquals("3306", status4.getPort().toString());
        assertTrue(status4.isPoolCreated());

//        restore
        SwitchableDataSourceStatus status5 = dataSource.restore();
        Thread.sleep(2000);
        assertEquals("onRestoreSuccess", listener.getOnCallMethodName());
        assertTrue(status5.isForceSwitched());
        assertEquals(IPHOST, status5.getHostName().toLowerCase());
        assertEquals("3306", status5.getPort().toString());
        assertTrue(status5.isPoolCreated());

//        getStatus
        SwitchableDataSourceStatus status6 = dataSource.getStatus();
        assertFalse(status6.isForceSwitched());
        assertEquals(DOMAINHOST, status6.getHostName().toLowerCase());
        assertEquals("3306", status6.getPort().toString());
        assertTrue(status6.isPoolCreated());
    }

    @Test
    public void testForceSwitchToInvalidHost() throws Exception {

        IDataSourceConfigureProvider provider = new MockDataSourceConfigureProvider();
        ForceSwitchableDataSource dataSource = new ForceSwitchableDataSource(provider);
        MockSwitchListener listener = new MockSwitchListener();
        dataSource.addListener(listener);

//        first forceSwitch
        DataSourceConfigure dataSourceConfigure1 = new DataSourceConfigure();
        SwitchableDataSourceStatus status1 = dataSource.forceSwitch(SerializableDataSourceConfig.valueOf(dataSourceConfigure1), INVALIDHOST, 3306);
        assertNull(listener.getOnCallMethodName());
        assertFalse(status1.isForceSwitched());
        assertEquals(IPHOST, status1.getHostName().toLowerCase());
        assertEquals("3306", status1.getPort().toString());
        assertTrue(status1.isPoolCreated());

//        getStatus
        SwitchableDataSourceStatus status11 = dataSource.getStatus();
        assertFalse(status11.isForceSwitched());
        assertEquals(IPHOST, status11.getHostName().toLowerCase());
        assertEquals("3306", status11.getPort().toString());
        assertTrue(status11.isPoolCreated());

        Thread.sleep(4000);
        assertEquals("onForceSwitchFail", listener.getOnCallMethodName());

//        getStatus
        SwitchableDataSourceStatus status2 = dataSource.getStatus();
        assertFalse(status2.isForceSwitched());
        assertEquals(IPHOST, status2.getHostName().toLowerCase());
        assertEquals("3306", status2.getPort().toString());
        assertTrue(status2.isPoolCreated());

//        second forceSwitch
        DataSourceConfigure dataSourceConfigure3 = new DataSourceConfigure();
        SwitchableDataSourceStatus status3 = dataSource.forceSwitch(SerializableDataSourceConfig.valueOf(dataSourceConfigure3), DOMAINHOST, 3306);
        Thread.sleep(2000);
        assertEquals("onForceSwitchSuccess", listener.getOnCallMethodName());
        assertFalse(status3.isForceSwitched());
        assertEquals(IPHOST, status3.getHostName());
        assertEquals("3306", status3.getPort().toString());
        assertTrue(status3.isPoolCreated());

//        getStatus
        SwitchableDataSourceStatus status4 = dataSource.getStatus();
        assertTrue(status4.isForceSwitched());
        assertEquals(DOMAINHOST, status4.getHostName().toLowerCase());
        assertEquals("3306", status4.getPort().toString());
        assertTrue(status4.isPoolCreated());
    }

    @Test
    public void testConfigChangedAfterForceSwitch() throws Exception {
        Properties properties = new Properties();
        properties.setProperty(USER_NAME, "root");
        properties.setProperty(PASSWORD, "!QAZ@WSX1qaz2wsx");
        properties.setProperty(CONNECTION_URL, "jdbc:mysql://DST56614:3306/llj_test?useUnicode=true&characterEncoding=UTF-8;");
        properties.setProperty(DRIVER_CLASS_NAME, "com.mysql.jdbc.Driver");

        DataSourceConfigure oldConfigure = new DataSourceConfigure("DalService2DB_w", properties);

        Properties newProperties = new Properties();
        newProperties.setProperty(USER_NAME, "root");
        newProperties.setProperty(PASSWORD, "!QAZ@WSX1qaz2wsx");
        newProperties.setProperty(CONNECTION_URL, "jdbc:mysql://10.32.20.128:3306/llj_test?useUnicode=true&characterEncoding=UTF-8;");
        newProperties.setProperty(DRIVER_CLASS_NAME, "com.mysql.jdbc.Driver");

        DataSourceConfigure newConfigure = new DataSourceConfigure("DalService2DB_w", newProperties);

        IDataSourceConfigureProvider provider = new MockDataSourceConfigureProvider();
        ForceSwitchableDataSource dataSource = new ForceSwitchableDataSource(provider);
        MockSwitchListener listener = new MockSwitchListener();
        dataSource.addListener(listener);

        DataSourceConfigure dataSourceConfigure = new DataSourceConfigure();
        dataSource.forceSwitch(SerializableDataSourceConfig.valueOf(dataSourceConfigure), DOMAINHOST, 3306);

        DataSourceConfigureChangeEvent event = new DataSourceConfigureChangeEvent("DalService2DB_w", newConfigure, oldConfigure);
        dataSource.configChanged(event);

        Thread.sleep(2000);
        SwitchableDataSourceStatus status = dataSource.getStatus();
        assertTrue(status.isForceSwitched());
        assertEquals(DOMAINHOST, status.getHostName());
    }

    @Test
    public void testToString() throws Exception {
        IDataSourceConfigureProvider provider = new MockDataSourceConfigureProvider();
        ForceSwitchableDataSource dataSource = new ForceSwitchableDataSource(provider);
        SwitchableDataSourceStatus status = dataSource.getStatus();
        assertEquals("isForceSwitched: false, poolCreated: true, hostName: 10.32.20.128, port: 3306", status.toString());
    }

    @Test
    public void testProviderThrowException() throws Exception {
        IDataSourceConfigureProvider provider = new ModifyDataSourceConfigureProvider();
        ForceSwitchableDataSource dataSource = new ForceSwitchableDataSource(provider);
        MockSwitchListener listener = new MockSwitchListener();
        dataSource.addListener(listener);

        assertTrue("nullDataSource".equalsIgnoreCase(dataSource.getSingleDataSource().getName()));
        SwitchableDataSourceStatus status0 = dataSource.getStatus();
        assertFalse(status0.isForceSwitched());
        assertFalse(status0.isPoolCreated());

        assertNull(dataSource.getSingleDataSource().getDataSourceConfigure().getConnectionUrl());
        assertNull(dataSource.getSingleDataSource().getDataSourceConfigure().getUserName());
        assertNull(dataSource.getSingleDataSource().getDataSourceConfigure().getPassword());

        Properties properties = new Properties();
        properties.setProperty(USER_NAME, "root");
        properties.setProperty(PASSWORD, "!QAZ@WSX1qaz2wsx");
        properties.setProperty(CONNECTION_URL, "jdbc:mysql://10.32.20.128:3306/llj_test?useUnicode=true&characterEncoding=UTF-8;");
        properties.setProperty(DRIVER_CLASS_NAME, "com.mysql.jdbc.Driver");
        DataSourceConfigure dataSourceConfigure = new DataSourceConfigure("DalService2DB_w", properties);
        dataSource.forceSwitch(SerializableDataSourceConfig.valueOf(dataSourceConfigure), DOMAINHOST, 3306);
        Thread.sleep(2000);

        assertEquals("onForceSwitchSuccess", listener.getOnCallMethodName());
        SwitchableDataSourceStatus status1 = dataSource.getStatus();
        assertTrue(status1.isForceSwitched());
        assertTrue(status1.isPoolCreated());
        assertEquals(DOMAINHOST, status1.getHostName().toLowerCase());
        assertEquals("3306", status1.getPort().toString());
        assertEquals("root", dataSource.getSingleDataSource().getDataSourceConfigure().getUserName());
        assertEquals("!QAZ@WSX1qaz2wsx", dataSource.getSingleDataSource().getDataSourceConfigure().getPassword());
        assertEquals("jdbc:mysql://10.32.20.128:3306/llj_test?useUnicode=true&characterEncoding=UTF-8;", dataSource.getSingleDataSource().getName());

        dataSource.forceSwitch(SerializableDataSourceConfig.valueOf(dataSourceConfigure), INVALIDHOST, 3306);
        Thread.sleep(20000);

        assertEquals("onForceSwitchFail", listener.getOnCallMethodName());
        SwitchableDataSourceStatus status2 = dataSource.getStatus();

        assertTrue(status2.isForceSwitched());
        assertTrue(status2.isPoolCreated());
        assertEquals(DOMAINHOST, status2.getHostName().toLowerCase());
        assertEquals("3306", status2.getPort().toString());

        Properties properties1 = new Properties();
        properties1.setProperty(USER_NAME, "root");
        properties1.setProperty(PASSWORD, "!QAZ@WSX1qaz2wsx");
        properties1.setProperty(CONNECTION_URL, "jdbc:mysql://dst56614:3306/llj_test?useUnicode=true&characterEncoding=UTF-8;");
        properties1.setProperty(DRIVER_CLASS_NAME, "com.mysql.jdbc.Driver");
        DataSourceConfigure dataSourceConfigure1 = new DataSourceConfigure("DalService2DB_w", properties1);
        dataSource.forceSwitch(SerializableDataSourceConfig.valueOf(dataSourceConfigure1), IPHOST, 3306);
        Thread.sleep(10000);

        assertEquals("onForceSwitchSuccess", listener.getOnCallMethodName());
        SwitchableDataSourceStatus status3 = dataSource.getStatus();
        assertEquals(IPHOST, status3.getHostName().toLowerCase());
        assertTrue(status3.isForceSwitched());
        assertTrue(status3.isPoolCreated());
        assertEquals("jdbc:mysql://10.32.20.128:3306/llj_test?useUnicode=true&characterEncoding=UTF-8;", dataSource.getSingleDataSource().getName());
        assertEquals("3306", status3.getPort().toString());

        dataSource.restore();
        Thread.sleep(4000);

        SwitchableDataSourceStatus status4 = dataSource.getStatus();
        assertTrue(status4.isForceSwitched());
        assertTrue(status4.isPoolCreated());
        assertEquals(IPHOST, status4.getHostName().toLowerCase());
        assertEquals("3306", status4.getPort().toString());
    }

    @Test
    public void testForceSwitchStatus() throws Exception {
        IDataSourceConfigureProvider provider = new MockDataSourceConfigureProvider();
        ForceSwitchableDataSource dataSource = new ForceSwitchableDataSource(provider);
        MockSwitchListener listener = new MockSwitchListener();
        dataSource.addListener(listener);

        DataSourceConfigure dataSourceConfigure1 = new DataSourceConfigure();
        dataSource.forceSwitch(SerializableDataSourceConfig.valueOf(dataSourceConfigure1), INVALIDHOST, 3306);

        SwitchableDataSourceStatus status1 = dataSource.getStatus();
        assertFalse(status1.isForceSwitched());

        dataSource.forceSwitch(SerializableDataSourceConfig.valueOf(dataSourceConfigure1), INVALIDHOST, 3306);
        SwitchableDataSourceStatus status2 = dataSource.getStatus();
        assertFalse(status2.isForceSwitched());

        Thread.sleep(5000);
        SwitchableDataSourceStatus status3 = dataSource.getStatus();
        assertFalse(status3.isForceSwitched());

        dataSource.forceSwitch(SerializableDataSourceConfig.valueOf(dataSourceConfigure1), DOMAINHOST, 3306);

        dataSource.forceSwitch(SerializableDataSourceConfig.valueOf(dataSourceConfigure1), INVALIDHOST, 3306);

        Thread.sleep(3000);
        SwitchableDataSourceStatus status4 = dataSource.getStatus();
        assertTrue(status4.isForceSwitched());

        dataSource.restore();
        Thread.sleep(2000);
        SwitchableDataSourceStatus status5 = dataSource.getStatus();
        assertFalse(status5.isForceSwitched());

        dataSource.forceSwitch(SerializableDataSourceConfig.valueOf(dataSourceConfigure1), INVALIDHOST, 3306);
        Thread.sleep(3000);
        SwitchableDataSourceStatus status6 = dataSource.getStatus();
        assertFalse(status6.isForceSwitched());
    }

    @Test
    public void testMGRJDBCUrlParser() {
        String mgr_url = "jdbc:mysql:replication://address=(type=master)(protocol=tcp)(host=10.2.7.196)(port=3306),address=((type=master)(protocol=tcp)(host=10.2.7.184)(port=3306),address=((type=master)(protocol=tcp)(host=10.2.7.187)(port=3306)/kevin?useUnicode=true&characterEncoding=UTF-8";
        String url = ConnectionStringParser.replaceHostAndPort(mgr_url, "127.0.0.1", "12345");
        Assert.assertEquals("jdbc:mysql://127.0.0.1:12345/kevin?useUnicode=true&characterEncoding=UTF-8", url);
    }

    @Test
    public void testSwitchMGRToNormal() throws Exception {
        IDataSourceConfigureProvider nullProvider = new ModifyDataSourceConfigureProvider();
        ForceSwitchableDataSource nullDataSource = new ForceSwitchableDataSource(nullProvider);
        Properties properties = new Properties();
        properties.setProperty(USER_NAME, "f_xie");
        properties.setProperty(PASSWORD, "123456");
        properties.setProperty(CONNECTION_URL, "jdbc:mysql:replication://address=(type=master)(protocol=tcp)(host=10.2.7.196)(port=3306),address=((type=master)(protocol=tcp)(host=10.2.7.184)(port=3306),address=((type=master)(protocol=tcp)(host=10.2.7.187)(port=3306)/kevin");
        properties.setProperty(DRIVER_CLASS_NAME, "com.mysql.jdbc.Driver");
        DataSourceConfigure dataSourceConfigure1 = new DataSourceConfigure("mgr", properties);
        nullDataSource.forceSwitch(SerializableDataSourceConfig.valueOf(dataSourceConfigure1), "127.0.0.1", 3306);
        Thread.sleep(3000);
        String mgrUrl = nullDataSource.getSingleDataSource().getDataSourceConfigure().getConnectionUrl();
        Assert.assertEquals("jdbc:mysql://127.0.0.1:3306/kevin", mgrUrl);

        IDataSourceConfigureProvider provider = new MockMgrDataSourceConfigureProvider();
        ForceSwitchableDataSource dataSource = new ForceSwitchableDataSource(provider);

        //Thread.sleep(3000);

//        SwitchableDataSourceStatus status = dataSource.getStatus();
//        Assert.assertFalse(status.isForceSwitched());
//        Assert.assertNull(status.getHostName());
//        Assert.assertNull(status.getPort());

        Properties newProperties = new Properties();
        newProperties.setProperty(USER_NAME, "f_xie");
        newProperties.setProperty(PASSWORD, "123456");
        newProperties.setProperty(CONNECTION_URL, "jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=UTF-8;");
        newProperties.setProperty(DRIVER_CLASS_NAME, "com.mysql.jdbc.Driver");
        DataSourceConfigure dataSourceConfigure = new DataSourceConfigure("normal", newProperties);
        dataSource.forceSwitch(SerializableDataSourceConfig.valueOf(dataSourceConfigure), "127.0.0.1", 3306);

        Thread.sleep(3000);
        SwitchableDataSourceStatus status1 = dataSource.getStatus();
        Assert.assertTrue(status1.isForceSwitched());
        Assert.assertEquals("127.0.0.1", status1.getHostName());
        Assert.assertEquals(3306, status1.getPort().intValue());

        dataSource.restore();
        Thread.sleep(3000);
        SwitchableDataSourceStatus status2 = dataSource.getStatus();
        Assert.assertFalse(status2.isForceSwitched());
        String url = dataSource.getSingleDataSource().getDataSourceConfigure().getConnectionUrl();
        Assert.assertEquals("jdbc:mysql:replication://address=(type=master)(protocol=tcp)(host=10.2.7.196)(port=3306),address=((type=master)(protocol=tcp)(host=10.2.7.184)(port=3306),address=((type=master)(protocol=tcp)(host=10.2.7.187)(port=3306)/kevin", url);
    }
}
