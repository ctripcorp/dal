package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.configure.*;
import org.junit.Test;

import java.util.Properties;

import static com.ctrip.platform.dal.dao.configure.DataSourceConfigureConstants.*;
import static org.junit.Assert.*;

public class ForceSwitchableDataSourceTest {
    private final static String IPHOST = "10.32.20.139";
    private final static String DOMAINHOST = "dst56614";
    private final static String INVALIDHOST = "1.1.1.1";


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
        assertTrue(status1.isConnected());

        SwitchableDataSourceStatus status2 = dataSource.getStatus();
        assertFalse(status2.isForceSwitched());
        assertEquals(IPHOST, status2.getHostName().toLowerCase());
        assertEquals("3306", status2.getPort().toString());
        assertTrue(status2.isConnected());
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
        assertTrue(status0.isConnected());

//        first forceSwitch
        SwitchableDataSourceStatus status1 = dataSource.forceSwitch(DOMAINHOST, 3306);
        Thread.sleep(2000);
        assertEquals("onForceSwitchSuccess", listener.getOnCallMethodName());
        assertFalse(status1.isForceSwitched());
        assertEquals(IPHOST, status1.getHostName().toLowerCase());
        assertEquals("3306", status1.getPort().toString());
        assertTrue(status1.isConnected());

//        getStatus
        SwitchableDataSourceStatus status2 = dataSource.getStatus();
        assertTrue(status2.isForceSwitched());
        assertEquals(DOMAINHOST, status2.getHostName().toLowerCase());
        assertEquals("3306", status2.getPort().toString());
        assertTrue(status2.isConnected());

//        second forceSwitch
        SwitchableDataSourceStatus status3 = dataSource.forceSwitch(IPHOST, 3306);
        Thread.sleep(2000);
        assertEquals("onForceSwitchSuccess", listener.getOnCallMethodName());
        assertTrue(status3.isForceSwitched());
        assertEquals(DOMAINHOST, status3.getHostName().toLowerCase());
        assertEquals("3306", status3.getPort().toString());
        assertTrue(status3.isConnected());

//        getStatus
        SwitchableDataSourceStatus status4 = dataSource.getStatus();
        assertTrue(status4.isForceSwitched());
        assertEquals(IPHOST, status4.getHostName().toLowerCase());
        assertEquals("3306", status4.getPort().toString());
        assertTrue(status4.isConnected());

//        restore
        SwitchableDataSourceStatus status5 = dataSource.restore();
        Thread.sleep(2000);
        assertEquals("onRestoreSuccess", listener.getOnCallMethodName());
        assertTrue(status5.isForceSwitched());
        assertEquals(IPHOST, status5.getHostName().toLowerCase());
        assertEquals("3306", status5.getPort().toString());
        assertTrue(status5.isConnected());

//        getStatus
        SwitchableDataSourceStatus status6 = dataSource.getStatus();
        assertFalse(status6.isForceSwitched());
        assertEquals(DOMAINHOST, status6.getHostName().toLowerCase());
        assertEquals("3306", status6.getPort().toString());
        assertTrue(status6.isConnected());
    }

    @Test
    public void testForceSwitchToInvalidHost() throws Exception {

        IDataSourceConfigureProvider provider = new MockDataSourceConfigureProvider();
        ForceSwitchableDataSource dataSource = new ForceSwitchableDataSource(provider);
        MockSwitchListener listener = new MockSwitchListener();
        dataSource.addListener(listener);

//        first forceSwitch
        SwitchableDataSourceStatus status1 = dataSource.forceSwitch(INVALIDHOST, 3306);
        assertNull(listener.getOnCallMethodName());
        assertFalse(status1.isForceSwitched());
        assertEquals(IPHOST, status1.getHostName().toLowerCase());
        assertEquals("3306", status1.getPort().toString());
        assertTrue(status1.isConnected());

//        getStatus
        SwitchableDataSourceStatus status11 = dataSource.getStatus();
        assertTrue(status11.isForceSwitched());
        assertEquals(INVALIDHOST, status11.getHostName());
        assertEquals("3306", status11.getPort().toString());
        assertFalse(status11.isConnected());

        Thread.sleep(4000);
        assertEquals("onForceSwitchFail", listener.getOnCallMethodName());

//        getStatus
        SwitchableDataSourceStatus status2 = dataSource.getStatus();
        assertTrue(status2.isForceSwitched());
        assertEquals(INVALIDHOST, status2.getHostName());
        assertEquals("3306", status2.getPort().toString());
        assertFalse(status2.isConnected());

//        second forceSwitch
        SwitchableDataSourceStatus status3 = dataSource.forceSwitch(IPHOST, 3306);
        Thread.sleep(2000);
        assertEquals("onForceSwitchSuccess", listener.getOnCallMethodName());
        assertTrue(status3.isForceSwitched());
        assertEquals(INVALIDHOST, status3.getHostName());
        assertEquals("3306", status3.getPort().toString());
        assertFalse(status3.isConnected());

//        getStatus
        SwitchableDataSourceStatus status4 = dataSource.getStatus();
        assertTrue(status4.isForceSwitched());
        assertEquals(IPHOST, status4.getHostName().toLowerCase());
        assertEquals("3306", status4.getPort().toString());
        assertTrue(status4.isConnected());
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
        newProperties.setProperty(CONNECTION_URL, "jdbc:mysql://10.32.20.139:3306/llj_test?useUnicode=true&characterEncoding=UTF-8;");
        newProperties.setProperty(DRIVER_CLASS_NAME, "com.mysql.jdbc.Driver");

        DataSourceConfigure newConfigure = new DataSourceConfigure("DalService2DB_w", newProperties);

        IDataSourceConfigureProvider provider = new MockDataSourceConfigureProvider();
        ForceSwitchableDataSource dataSource = new ForceSwitchableDataSource(provider);
        MockSwitchListener listener = new MockSwitchListener();
        dataSource.addListener(listener);

        dataSource.forceSwitch(DOMAINHOST, 3306);

        DataSourceConfigureChangeEvent event = new DataSourceConfigureChangeEvent("DalService2DB_w", newConfigure, oldConfigure);
        dataSource.configChanged(event);

        SwitchableDataSourceStatus status = dataSource.getStatus();
        assertTrue(status.isForceSwitched());
        assertEquals(DOMAINHOST, status.getHostName());
    }
}
