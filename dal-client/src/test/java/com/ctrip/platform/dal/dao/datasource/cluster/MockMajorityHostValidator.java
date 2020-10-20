package com.ctrip.platform.dal.dao.datasource.cluster;

import com.ctrip.platform.dal.exceptions.DalConfigException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MockMajorityHostValidator extends MajorityHostValidator {

    public volatile HashMap<HostSpec, MysqlStatus> mysqlServer = new HashMap<>();

    {
        HostSpec hostSpec1 = HostSpec.of("local", 3306);
        HostSpec hostSpec2 = HostSpec.of("local", 3307);
        HostSpec hostSpec3 = HostSpec.of("local", 3308);
        mysqlServer.put(hostSpec1, MysqlStatus.ok);
        mysqlServer.put(hostSpec2, MysqlStatus.ok);
        mysqlServer.put(hostSpec3, MysqlStatus.ok);
    }

    enum MysqlStatus{
        ok, unknown, fail
    }

    public MockMajorityHostValidator(HashMap<HostSpec, MysqlStatus> mysqlServer) {
        this.mysqlServer = mysqlServer;
    }

    public MockMajorityHostValidator(ConnectionFactory factory, Set<HostSpec> configuredHosts, long failOverTime, long blackListTimeOut) {
        super(factory, configuredHosts, failOverTime, blackListTimeOut);
    }

    @Override
    protected boolean validate(Connection connection, int clusterHostCount) throws SQLException {
        MockConnection mockConnection = (MockConnection) connection;
        HostSpec host = mockConnection.getHost();

        if (MysqlStatus.unknown.equals(mysqlServer.get(host)))
            throw new SQLException("");
        return MysqlStatus.ok.equals(mysqlServer.get(host));
    }

    @Override
    protected HostSpec getHostSpecFromConnection(Connection connection) {
        MockConnection mockConnection = (MockConnection)connection;
        return mockConnection.getHost();
    }
}
