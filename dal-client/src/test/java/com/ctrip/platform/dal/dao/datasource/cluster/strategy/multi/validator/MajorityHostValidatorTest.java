package com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.validator;

import com.ctrip.platform.dal.dao.base.MockConnection;
import com.ctrip.platform.dal.dao.base.MockDefaultHostConnection;
import com.ctrip.platform.dal.dao.base.MockResultSet;
import com.ctrip.platform.dal.dao.base.MockStatement;
import com.ctrip.platform.dal.dao.datasource.cluster.DefaultHostConnection;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class MajorityHostValidatorTest extends AbstractHostValidatorTest {

    @Test
    public void availableTest() throws InterruptedException, SQLException {
        MockMajorityHostValidator validator = new MockMajorityHostValidator(configuredHost, orderedHosts, failOverTime, blackListTimeOut, fixedValidatePeriod);

        assertEquals(true, validator.available(hostSpec1));

        validator.mysqlServer.put(hostSpec1, MockMajorityHostValidator.MysqlStatus.fail);
        assertEquals(true, validator.available(hostSpec1));
        validator.validate(new MockDefaultHostConnection(hostSpec1));
        assertEquals(false, validator.available(hostSpec1));

        validator.mysqlServer.put(hostSpec1, MockMajorityHostValidator.MysqlStatus.unknown);
        assertEquals(false, validator.available(hostSpec1));
        try {
            validator.validate(new MockDefaultHostConnection(hostSpec1));
        }catch (Exception e) { }
        assertEquals(false, validator.available(hostSpec1));
        TimeUnit.MILLISECONDS.sleep(failOverTime);
        assertEquals(false, validator.available(hostSpec1));

    }

    @Test
    public void changeZone() throws SQLException, InterruptedException {
        MockMajorityHostValidator validator = new MockMajorityHostValidator(configuredHost, orderedHosts, failOverTime, blackListTimeOut, fixedValidatePeriod);
        assertEquals(true, validator.available(hostSpec1));
        MockDefaultHostConnection mockDefaultHostConnection1 = new MockDefaultHostConnection(hostSpec1);
        MockDefaultHostConnection mockDefaultHostConnection2 = new MockDefaultHostConnection(hostSpec2);
        MockDefaultHostConnection mockDefaultHostConnection3 = new MockDefaultHostConnection(hostSpec3);

        validator.mysqlServer.put(hostSpec2, MockMajorityHostValidator.MysqlStatus.fail);
        validator.mysqlServer.put(hostSpec3, MockMajorityHostValidator.MysqlStatus.fail);

        assertEquals(false, validator.validate(mockDefaultHostConnection1));
        assertEquals(true, validator.available(hostSpec2));

        validator.mysqlServer.put(hostSpec1, MockMajorityHostValidator.MysqlStatus.fail);
        validator.mysqlServer.put(hostSpec2, MockMajorityHostValidator.MysqlStatus.ok);
        validator.mysqlServer.put(hostSpec3, MockMajorityHostValidator.MysqlStatus.ok);
        assertEquals(false, validator.validate(mockDefaultHostConnection1));
        assertEquals(true, validator.validate(mockDefaultHostConnection2));
        assertEquals(true, validator.validate(mockDefaultHostConnection3));

        assertEquals(false, validator.available(hostSpec1));
        TimeUnit.MILLISECONDS.sleep(failOverTime + 10);
        assertEquals(true, validator.available(hostSpec1));


    }

    @Test
    public void failToUnKnown() throws SQLException, InterruptedException {
        MockMajorityHostValidator validator = new MockMajorityHostValidator(configuredHost, orderedHosts, failOverTime, blackListTimeOut, fixedValidatePeriod);
        validator.mysqlServer.put(hostSpec1, MockMajorityHostValidator.MysqlStatus.fail);
        MockDefaultHostConnection mockDefaultHostConnection1 = new MockDefaultHostConnection(hostSpec1);

        assertEquals(false, validator.validate(mockDefaultHostConnection1));
        assertEquals(false, validator.available(hostSpec1));

        validator.mysqlServer.put(hostSpec1, MockMajorityHostValidator.MysqlStatus.unknown);
        TimeUnit.MILLISECONDS.sleep(failOverTime);
        validator.triggerValidate();
        TimeUnit.MILLISECONDS.sleep(10);
        assertEquals(true, validator.available(hostSpec1));
    }

    @Test
    public void okToUnknown() throws SQLException, InterruptedException {
        MockMajorityHostValidator validator = new MockMajorityHostValidator(configuredHost, orderedHosts, failOverTime, blackListTimeOut, fixedValidatePeriod);
        MockDefaultHostConnection mockDefaultHostConnection1 = new MockDefaultHostConnection(hostSpec1);

        assertEquals(true, validator.validate(mockDefaultHostConnection1));
        assertEquals(true, validator.available(hostSpec1));

        validator.mysqlServer.put(hostSpec1, MockMajorityHostValidator.MysqlStatus.unknown);
        TimeUnit.MILLISECONDS.sleep(failOverTime);
        validator.triggerValidate();
        TimeUnit.MILLISECONDS.sleep(failOverTime);
        assertEquals(true, validator.available(hostSpec1));
    }

    @Test
    public void failToNullConnection() throws SQLException {
        MockMajorityHostValidator validator = new MockMajorityHostValidator(configuredHost, orderedHosts, failOverTime, blackListTimeOut, fixedValidatePeriod);
        DefaultHostConnection defaultHostConnection = new DefaultHostConnection(null, hostSpec1);

        assertEquals(false, validator.validate(defaultHostConnection));
        assertEquals(true, validator.available(hostSpec1));
    }

    @Test
    public void doubleCheckOnlineStatusTest() throws SQLException {
        // currentMemberId is empty
        MockMajorityHostValidator validator = new MockMajorityHostValidator(configuredHost, orderedHosts, failOverTime, blackListTimeOut, fixedValidatePeriod);
        assertEquals(false, validator.doubleCheckOnlineStatus("", hostSpec1));

        //host not in pre and black list
        assertEquals(false, validator.doubleCheckOnlineStatus("1234", hostSpec1));

        // hostSpec2 is need to remove from pre list
        validator.mysqlServer.put(hostSpec2, MockMajorityHostValidator.MysqlStatus.unknown);
        MockDefaultHostConnection mockDefaultHostConnection2 = new MockDefaultHostConnection(hostSpec2);
        try {
            validator.validateAndUpdate(mockDefaultHostConnection2, hostSpec2, 3);
        }catch (Exception e){}

        Connection mockConnectionHost1 = new MockConnection();
        ResultSet resultSet1 = new MockResultSet();
        HashMap<String, Object> host1Map = new HashMap<>();
        host1Map.put("CURRENT_MEMBER_ID", "host1");
        host1Map.put("MEMBER_STATE", "online");
        host1Map.put("MEMBER_ID", "host2");
        ((MockResultSet)resultSet1).result.add(host1Map);
        ((MockStatement)((MockConnection)mockConnectionHost1).statement).result.put("default", resultSet1);

        Connection mockConnectionHost3 = new MockConnection();
        ResultSet resultSet3 = new MockResultSet();
        HashMap<String, Object> host3Map = new HashMap<>();
        host3Map.put("CURRENT_MEMBER_ID", "host3");
        host3Map.put("MEMBER_STATE", "online");
        host3Map.put("MEMBER_ID", "host2");
        ((MockResultSet)resultSet3).result.add(host3Map);
        ((MockStatement)((MockConnection)mockConnectionHost3).statement).result.put("default", resultSet3);

        ((MockResultSet)resultSet1).resetIndex();
        ((MockResultSet)resultSet3).resetIndex();
        validator.connectionMap.put(hostSpec1, mockConnectionHost1);
        validator.connectionMap.put(hostSpec3, mockConnectionHost3);
        assertEquals(true, validator.doubleCheckOnlineStatus("host2", hostSpec2));

        ((MockResultSet)resultSet1).resetIndex();
        ((MockResultSet)resultSet3).resetIndex();
        host1Map.put("MEMBER_STATE", "error");
        host3Map.put("MEMBER_STATE", "error");
        assertEquals(false, validator.doubleCheckOnlineStatus("host2", hostSpec2));

        ((MockResultSet)resultSet1).resetIndex();
        ((MockResultSet)resultSet3).resetIndex();
        host1Map.put("MEMBER_STATE", "online");
        assertEquals(true, validator.doubleCheckOnlineStatus("host2", hostSpec2));

        ((MockResultSet)resultSet1).resetIndex();
        ((MockResultSet)resultSet3).resetIndex();
        host3Map.put("default_timeout", "5");
        assertEquals(true, validator.doubleCheckOnlineStatus("host2", hostSpec2));
        validator.destroy();
    }
}