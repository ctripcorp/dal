package com.ctrip.platform.dal.dao.datasource.cluster;

import com.ctrip.platform.dal.exceptions.InvalidConnectionException;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class MajorityHostValidatorTest {

    private long failOverTime = 5000;
    private long blackListTimeOut = 5000;
    private long fixedValidatePeriod = 15000;
    private ExecutorService service = Executors.newFixedThreadPool(16);
    private HostSpec hostSpec1 = HostSpec.of("local", 3306);
    private HostSpec hostSpec2 = HostSpec.of("local", 3307);
    private HostSpec hostSpec3 = HostSpec.of("local", 3308);
    Set<HostSpec> configuredHost = new HashSet<>();
    List<HostSpec> orderedHosts = new ArrayList<>();

    {
        configuredHost.add(hostSpec1);
        configuredHost.add(hostSpec2);
        configuredHost.add(hostSpec3);
        orderedHosts.add(hostSpec1);
        orderedHosts.add(hostSpec2);
        orderedHosts.add(hostSpec3);

    }

    @Test
    public void availableTest() throws InterruptedException, SQLException {
        MockMajorityHostValidator validator = new MockMajorityHostValidator(buildConnectionFactory(), configuredHost, orderedHosts, failOverTime, blackListTimeOut, fixedValidatePeriod);

        assertEquals(true, validator.available(hostSpec1));

        validator.mysqlServer.put(hostSpec1, MockMajorityHostValidator.MysqlStatus.fail);
        assertEquals(true, validator.available(hostSpec1));
        validator.validate(new MockConnection(hostSpec1));
        assertEquals(false, validator.available(hostSpec1));
        TimeUnit.MILLISECONDS.sleep(blackListTimeOut);
        assertEquals(false, validator.available(hostSpec1));
        TimeUnit.MILLISECONDS.sleep(blackListTimeOut);
        assertEquals(true, validator.available(hostSpec1));

        validator.mysqlServer.put(hostSpec1, MockMajorityHostValidator.MysqlStatus.unknown);
        assertEquals(true, validator.available(hostSpec1));
        try {
            validator.validate(new MockConnection(hostSpec1));
        }catch (Exception e) {
            assertEquals(e instanceof SQLException, true);
        }
        assertEquals(false, validator.available(hostSpec1));
        TimeUnit.MILLISECONDS.sleep(failOverTime);
        assertEquals(false, validator.available(hostSpec1));


        CountDownLatch countDownLatch = new CountDownLatch(100);
        validator.mysqlServer.put(hostSpec1, MockMajorityHostValidator.MysqlStatus.fail);
        for (int i=0; i< 100; i++) {
            service.submit(() -> {
                try {
                    validator.validate(new MockConnection(hostSpec1));
                }catch (Exception e) {
                }finally {
                    countDownLatch.countDown();
                }
            });
        }
        assertEquals(false, validator.available(hostSpec1));
        countDownLatch.await();
        TimeUnit.MILLISECONDS.sleep(blackListTimeOut);
        assertEquals(true, validator.available(hostSpec1));

        CountDownLatch second = new CountDownLatch(100);
        validator.mysqlServer.put(hostSpec1, MockMajorityHostValidator.MysqlStatus.unknown);
        for (int i=0; i<100; i++) {
            service.submit(() -> {
               try {
                   validator.validate(new MockConnection(hostSpec1));
               }catch (Exception e) {

               }finally {
                   second.countDown();
               }
            });
        }
        assertEquals(false, validator.available(hostSpec1));
        second.await();
        TimeUnit.MILLISECONDS.sleep(failOverTime);
        assertEquals(false, validator.available(hostSpec1));

    }

    private ConnectionFactory buildConnectionFactory() {
        return new ConnectionFactory() {
            @Override
            public Connection getPooledConnectionForHost(HostSpec host) throws SQLException, InvalidConnectionException {
                return null;
            }

            @Override
            public Connection createConnectionForHost(HostSpec host) throws SQLException, InvalidConnectionException {
                return new MockConnection(host);
            }
        };
    }

    @Test
    public void changeZone() throws SQLException, InterruptedException {
        MockMajorityHostValidator validator = new MockMajorityHostValidator(buildConnectionFactory(), configuredHost, orderedHosts, failOverTime, blackListTimeOut, fixedValidatePeriod);
        assertEquals(true, validator.available(hostSpec1));
        MockConnection mockConnection1 = new MockConnection(hostSpec1);
        MockConnection mockConnection2 = new MockConnection(hostSpec2);
        MockConnection mockConnection3 = new MockConnection(hostSpec3);

        validator.mysqlServer.put(hostSpec2, MockMajorityHostValidator.MysqlStatus.fail);
        validator.mysqlServer.put(hostSpec3, MockMajorityHostValidator.MysqlStatus.fail);

        assertEquals(false, validator.validate(mockConnection1));
        assertEquals(true, validator.available(hostSpec2));

        validator.mysqlServer.put(hostSpec1, MockMajorityHostValidator.MysqlStatus.fail);
        validator.mysqlServer.put(hostSpec2, MockMajorityHostValidator.MysqlStatus.ok);
        validator.mysqlServer.put(hostSpec3, MockMajorityHostValidator.MysqlStatus.ok);
        assertEquals(false, validator.validate(mockConnection1));
        assertEquals(true, validator.validate(mockConnection2));
        assertEquals(true, validator.validate(mockConnection3));

        assertEquals(false, validator.available(hostSpec1));
        TimeUnit.MILLISECONDS.sleep(failOverTime);
        assertEquals(true, validator.available(hostSpec1));


    }

    @Test
    public void failToUnKnown() throws SQLException, InterruptedException {
        MockMajorityHostValidator validator = new MockMajorityHostValidator(buildConnectionFactory(), configuredHost, orderedHosts, failOverTime, blackListTimeOut, fixedValidatePeriod);
        validator.mysqlServer.put(hostSpec1, MockMajorityHostValidator.MysqlStatus.fail);
        MockConnection mockConnection1 = new MockConnection(hostSpec1);

        assertEquals(false, validator.validate(mockConnection1));
        assertEquals(false, validator.available(hostSpec1));

        validator.mysqlServer.put(hostSpec1, MockMajorityHostValidator.MysqlStatus.unknown);
        TimeUnit.MILLISECONDS.sleep(failOverTime);
        validator.triggerValidate();
        TimeUnit.MILLISECONDS.sleep(10);
        assertEquals(false, validator.available(hostSpec1));
    }

    @Test
    public void okToUnknown() throws SQLException, InterruptedException {
        MockMajorityHostValidator validator = new MockMajorityHostValidator(buildConnectionFactory(), configuredHost, orderedHosts, failOverTime, blackListTimeOut, fixedValidatePeriod);
        MockConnection mockConnection1 = new MockConnection(hostSpec1);

        assertEquals(true, validator.validate(mockConnection1));
        assertEquals(true, validator.available(hostSpec1));

        validator.mysqlServer.put(hostSpec1, MockMajorityHostValidator.MysqlStatus.unknown);
        TimeUnit.MILLISECONDS.sleep(failOverTime);
        validator.triggerValidate();
        TimeUnit.MILLISECONDS.sleep(10);
        assertEquals(true, validator.available(hostSpec1));
    }

    @Test
    public void validate() throws SQLException, InterruptedException {
    }
}