package com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.ob;

import com.ctrip.framework.dal.cluster.client.base.HostSpec;
import com.ctrip.framework.dal.cluster.client.exception.HostNotExpectedException;
import com.ctrip.framework.dal.cluster.client.util.CaseInsensitiveProperties;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.datasource.cluster.HostConnection;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.RouteStrategy;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;

import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @Author limingdong
 * @create 2021/8/18
 */
public class CompositeRoundRobinStrategy extends ConcurrentHashMap<String, RouteStrategy> implements RouteStrategy {

    private static final String NO_ZONE_AVAILABLE = "Router::noZOneAvailable";

    private String zone;

    public CompositeRoundRobinStrategy() {
        this.zone = DalElementFactory.DEFAULT.getEnvUtils().getZone();
        if (zone != null) {
            this.zone = this.zone.toUpperCase();
        }
    }

    @Override
    public void init(Set<HostSpec> hostSpecs, CaseInsensitiveProperties strategyProperties) {
        throw new UnsupportedOperationException("CompositeRoundRobinAccessStrategy not support");
    }

    @Override
    public HostSpec pickNode(DalHints hints) throws HostNotExpectedException {
        try {
            return pickNodeInLocalZone(hints);
        } catch (Exception e) {
            return pickNodeNotInLocalZone(hints);
        }
    }

    private HostSpec pickNodeInLocalZone(DalHints hints) throws HostNotExpectedException {
        if (zone == null) {
            throw new HostNotExpectedException(NO_ZONE_AVAILABLE);
        }
        RouteStrategy routeStrategy = get(zone);
        return pickNodeInOneZone(routeStrategy, hints);
    }

    private HostSpec pickNodeNotInLocalZone(DalHints hints) throws HostNotExpectedException {
        for (Map.Entry<String, RouteStrategy> entry : entrySet()) {
            if (!entry.getKey().equalsIgnoreCase(zone)) {
                try {
                    return pickNodeInOneZone(entry.getValue(), hints);
                } catch (Exception e1) {
                    // nothing to do
                }
            }
        }

        throw new HostNotExpectedException(NO_HOST_AVAILABLE);
    }

    private HostSpec pickNodeInOneZone(RouteStrategy routeStrategy, DalHints dalHints) throws HostNotExpectedException {
        if (routeStrategy == null) {
            throw new HostNotExpectedException(NO_HOST_AVAILABLE);
        }

        return routeStrategy.pickNode(dalHints);
    }

    @Override
    public void dispose() {
        values().forEach(s -> s.dispose());
    }

    @Override
    public SQLException interceptException(SQLException sqlEx, HostConnection conn) {
        return sqlEx;
    }

    // for test
    protected void setZone(String zone) {
        if (zone != null) {
            this.zone = zone.toUpperCase();
        }
    }
}
