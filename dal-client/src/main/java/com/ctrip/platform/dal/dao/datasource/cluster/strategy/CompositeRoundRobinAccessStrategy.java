package com.ctrip.platform.dal.dao.datasource.cluster.strategy;

import com.ctrip.framework.dal.cluster.client.util.CaseInsensitiveProperties;
import com.ctrip.platform.dal.dao.datasource.cluster.ConnectionFactory;
import com.ctrip.platform.dal.dao.datasource.cluster.HostConnection;
import com.ctrip.platform.dal.dao.datasource.cluster.RequestContext;
import com.ctrip.platform.dal.dao.datasource.cluster.ShardMeta;
import com.ctrip.platform.dal.exceptions.DalException;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.ctrip.platform.dal.dao.datasource.cluster.strategy.AbstractMultiHostStrategy.NO_HOST_AVAILABLE;

/**
 * @Author limingdong
 * @create 2021/8/18
 */
public class CompositeRoundRobinAccessStrategy extends ConcurrentHashMap<String, MultiHostStrategy> implements MultiHostStrategy {

    @Override
    public void initialize(ShardMeta shardMeta, ConnectionFactory connFactory, CaseInsensitiveProperties strategyProperties) {
        throw new UnsupportedOperationException("LocalizedAccessStrategy not support");
    }

    @Override
    public HostConnection pickConnection(RequestContext request) throws SQLException {
        try {
            return pickConnectionInLocalZone(request);
        } catch (SQLException e) {
            return pickConnectionNotInLocalZone(request);
        }
    }


    private HostConnection pickConnectionInLocalZone(RequestContext request) throws SQLException {
        String zone = request.clientZone();
        MultiHostStrategy multiHostStrategy = get(zone);
        return pickConnectionInOneZone(multiHostStrategy, request);
    }

    private HostConnection pickConnectionNotInLocalZone(RequestContext request) throws SQLException {
        for (Map.Entry<String, MultiHostStrategy> entry : entrySet()) {
            if (!entry.getKey().equalsIgnoreCase(request.clientZone())) {
                try {
                    return pickConnectionInOneZone(entry.getValue(), request);
                } catch (SQLException e1) {
                    // nothing to do
                }
            }
        }

        throw new DalException(NO_HOST_AVAILABLE);
    }

    private HostConnection pickConnectionInOneZone(MultiHostStrategy multiHostStrategy, RequestContext request) throws SQLException {
        if (multiHostStrategy == null) {
            throw new DalException(NO_HOST_AVAILABLE);
        }

        return multiHostStrategy.pickConnection(request);
    }


    @Override
    public void destroy() {
        values().forEach(s -> s.destroy());
    }
}
