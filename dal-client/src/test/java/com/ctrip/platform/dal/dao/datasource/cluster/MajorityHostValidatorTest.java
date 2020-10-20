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
    private ExecutorService service = Executors.newFixedThreadPool(16);

    @Test
    public void availableTest() throws InterruptedException, SQLException {
        HostSpec hostSpec1 = HostSpec.of("local", 3306);
        HostSpec hostSpec2 = HostSpec.of("local", 3307);
        HostSpec hostSpec3 = HostSpec.of("local", 3308);
        Set<HostSpec> configuredHost = new HashSet<>();
        configuredHost.add(hostSpec1);
        configuredHost.add(hostSpec2);
        configuredHost.add(hostSpec3);
        MockMajorityHostValidator validator = new MockMajorityHostValidator(buildConnectionFactory(), configuredHost, failOverTime, blackListTimeOut);

        assertEquals(true, validator.available(hostSpec1));

        validator.mysqlServer.put(hostSpec1, MockMajorityHostValidator.MysqlStatus.fail);
        assertEquals(true, validator.available(hostSpec1));
        validator.validate(new MockConnection(hostSpec1));
        assertEquals(false, validator.available(hostSpec1));
        TimeUnit.MILLISECONDS.sleep(blackListTimeOut);
        assertEquals(true, validator.available(hostSpec1));
        validator.triggerValidate();
        TimeUnit.MILLISECONDS.sleep(1);
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
        assertEquals(true, validator.available(hostSpec1));
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
        assertEquals(true, validator.available(hostSpec1));
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
    public void validate() throws SQLException, InterruptedException {
    }
}