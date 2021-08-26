package com.ctrip.platform.dal.dao.datasource.cluster.strategy;

import com.ctrip.platform.dal.cluster.base.HostSpec;
import com.ctrip.platform.dal.cluster.util.CaseInsensitiveProperties;
import com.ctrip.platform.dal.dao.datasource.cluster.ConnectionFactory;
import com.ctrip.platform.dal.dao.datasource.cluster.HostConnection;
import com.ctrip.platform.dal.dao.datasource.cluster.ShardMeta;
import com.ctrip.platform.dal.dao.datasource.cluster.ZonedHostSorter;

import java.sql.SQLException;
import java.util.List;

public class OrderedAccessStrategy extends AbstractMultiHostStrategy implements MultiHostStrategy {

    private volatile HostSpec currentHost;

    @Override
    public void initialize(ShardMeta shardMeta, ConnectionFactory connFactory, CaseInsensitiveProperties strategyProperties) {
        super.initialize(shardMeta, connFactory, strategyProperties);
        this.currentHost = orderHosts.get(0);
    }

    @Override
    protected HostConnection tryPickConnection(HostSpec targetHost) throws SQLException {
        synchronized (this) {
            if (!targetHost.equals(currentHost)) {
                LOGGER.warn(String.format(CONNECTION_HOST_CHANGE, String.format(CHANGE_FROM_TO, currentHost.toString(), targetHost.toString())));
                LOGGER.logEvent(CAT_LOG_TYPE, String.format(CONNECTION_HOST_CHANGE, cluster), String.format(CHANGE_FROM_TO, currentHost.toString(), targetHost.toString()));
                currentHost = targetHost;
            }
        }
        return super.tryPickConnection(targetHost);
    }

    @Override
    protected void doBuildOrderHosts () {
        List<String> zoneOrder = strategyOptions.getStringList("zonesPriority", ",", null);
        ZonedHostSorter sorter = new ZonedHostSorter(zoneOrder);
        this.orderHosts = sorter.sort(configuredHosts);
    }
}
