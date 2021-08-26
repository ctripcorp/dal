package com.ctrip.platform.dal.dao.datasource.cluster.strategy;

import com.ctrip.platform.dal.cluster.base.HostSpec;
import com.ctrip.platform.dal.dao.datasource.cluster.ConnectionFactory;
import com.ctrip.platform.dal.dao.datasource.cluster.HostConnection;
import com.ctrip.platform.dal.exceptions.InvalidConnectionException;
import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @Author limingdong
 * @create 2021/8/18
 */
@FixMethodOrder(value = MethodSorters.NAME_ASCENDING)
public class RoundRobinAccessStrategyTest extends ShardMetaGenerator {

    private RoundRobinAccessStrategy accessStrategy;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        accessStrategy = new RoundRobinAccessStrategy();
    }

    @Test
    public void pickConnection_1() throws SQLException {
        accessStrategy.initialize(shardMeta, connectionFactory, caseInsensitiveProperties);

        HostConnection hostConnection = accessStrategy.pickConnection(requestContext);
        HostSpec hostSpec = hostConnection.getHost();
        Assert.assertEquals(getRequestZone(), hostSpec.zone());  // pick from local

        hostConnection = accessStrategy.pickConnection(requestContext);
        HostSpec hostSpec1 = hostConnection.getHost();
        Assert.assertEquals(getRequestZone(), hostSpec1.zone());  // pick from local

        hostConnection = accessStrategy.pickConnection(requestContext);
        HostSpec hostSpec2 = hostConnection.getHost();
        Assert.assertEquals(hostSpec, hostSpec2);  // pick from local

        hostConnection = accessStrategy.pickConnection(requestContext);
        HostSpec hostSpec3 = hostConnection.getHost();
        Assert.assertEquals(hostSpec1, hostSpec3);  // pick from local

        accessStrategy.destroy();
    }

    @Test(expected = SQLException.class)
    public void pickConnection_2_InvalidConnectionException() throws SQLException {

        ConnectionFactory localConnectionFactory = new ConnectionFactory() {
            @Override
            public Connection getPooledConnectionForHost(HostSpec host) throws InvalidConnectionException {
                throw new InvalidConnectionException();
            }

            @Override
            public Connection createConnectionForHost(HostSpec host) throws InvalidConnectionException {
                throw new InvalidConnectionException();
            }
        };

        RoundRobinAccessStrategy localAccessStrategy = new RoundRobinAccessStrategy();
        localAccessStrategy.initialize(shardMeta, localConnectionFactory, caseInsensitiveProperties);

        localAccessStrategy.pickConnection(requestContext);

        localAccessStrategy.destroy();
    }

    @Test(expected = SQLException.class)
    public void pickConnection_3_CommunicationsException() throws SQLException {

        ConnectionFactory localConnectionFactory = new ConnectionFactory() {
            @Override
            public Connection getPooledConnectionForHost(HostSpec host) throws InvalidConnectionException, CommunicationsException {
                throw new CommunicationsException(null, 0l, 0l, null);
            }

            @Override
            public Connection createConnectionForHost(HostSpec host) throws InvalidConnectionException, CommunicationsException {
                throw new CommunicationsException(null, 0l, 0l, null);
            }
        };

        RoundRobinAccessStrategy localAccessStrategy = new RoundRobinAccessStrategy();
        localAccessStrategy.initialize(shardMeta, localConnectionFactory, caseInsensitiveProperties);

        localAccessStrategy.pickConnection(requestContext);

        localAccessStrategy.destroy();
    }

    @Override
    protected void addRB() {
    }

    @Override
    protected void addXY() {
    }

}