package com.ctrip.platform.dal.dao.datasource.cluster.validator;

import com.ctrip.framework.dal.cluster.client.base.HostSpec;
import com.ctrip.platform.dal.dao.base.MockDefaultHostConnection;
import com.ctrip.platform.dal.dao.client.CustomConnection;
import com.ctrip.platform.dal.dao.datasource.cluster.ConnectionFactory;
import com.ctrip.platform.dal.exceptions.InvalidConnectionException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author limingdong
 * @create 2021/8/19
 */
public abstract class AbstractHostValidatorTest {

    protected long failOverTime = 1000;
    protected long blackListTimeOut = 1000;
    protected long fixedValidatePeriod = 3000;
    protected HostSpec hostSpec1 = HostSpec.of("local", 3306);
    protected HostSpec hostSpec2 = HostSpec.of("local", 3307);
    protected HostSpec hostSpec3 = HostSpec.of("local", 3308);
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

    public static ConnectionFactory buildConnectionFactory() {
        return new ConnectionFactory() {
            @Override
            public Connection getPooledConnectionForHost(HostSpec host) throws SQLException, InvalidConnectionException {
                return new CustomConnection();
            }

            @Override
            public Connection createConnectionForHost(HostSpec host) throws SQLException, InvalidConnectionException {
                return new MockDefaultHostConnection(host);
            }
        };
    }
}
