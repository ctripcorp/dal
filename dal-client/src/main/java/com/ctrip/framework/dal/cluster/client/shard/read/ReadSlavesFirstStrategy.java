package com.ctrip.framework.dal.cluster.client.shard.read;

import com.ctrip.framework.dal.cluster.client.base.HostSpec;
import com.ctrip.framework.dal.cluster.client.cluster.RouteStrategyEnum;
import com.ctrip.framework.dal.cluster.client.exception.HostNotExpectedException;
import com.ctrip.framework.dal.cluster.client.util.CaseInsensitiveProperties;
import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.datasource.cluster.HostConnection;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.ReadStrategy;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.helper.EnvUtils;

import java.sql.SQLException;
import java.util.*;

import static com.ctrip.framework.dal.cluster.client.cluster.RouteStrategyEnum.READ_SLAVES_FIRST;

public class ReadSlavesFirstStrategy implements ReadStrategy {

    protected static final EnvUtils envUtils = DalElementFactory.DEFAULT.getEnvUtils();
    protected HashMap<String, List<HostSpec>> hostMap = new HashMap<>();
    protected HashMap<RouteStrategyEnum, ReadStrategy> routeStrategies = new HashMap<>();
    protected final String ZONE_MISS = "%s hostspec of %s zone message missed.";

    private RouteStrategyEnum readStrategyEnum = READ_SLAVES_FIRST;
    protected Set<HostSpec> hostSpecs;
    protected CaseInsensitiveProperties strategyProperties;

    @Override
    public void init(Set<HostSpec> hostSpecs, CaseInsensitiveProperties strategyProperties) {
        this.hostSpecs = hostSpecs;
        this.strategyProperties = strategyProperties;
        List<HostSpec> masters = new ArrayList<>();
        List<HostSpec> slaves = new ArrayList<>();

        for (HostSpec hostSpec : hostSpecs) {
            if (hostSpec.isMaster())
                masters.add(hostSpec);
            else
                slaves.add(hostSpec);
        }

        hostMap.putIfAbsent(masterRole, masters);
        hostMap.putIfAbsent(slaveRole, slaves);
    }

    @Override
    public HostSpec pickRead(DalHints dalHints) throws HostNotExpectedException {
        if (dalHints.getRouteStrategy() != null)
            return dalHintsRoute(dalHints);

        if (dalHints.is(DalHintEnum.slaveOnly) && envUtils.isProd())
            return slaveOnly();

        List<HostSpec> slaves = hostMap.get(slaveRole);
        if (slaves == null || slaves.isEmpty())
            return pickMaster();

        return choseByRandom(slaves);
    }

    protected HostSpec pickMaster() {
        List<HostSpec> masters = getHostByRole(masterRole);
        return masters.iterator().next();
    }

    protected HostSpec slaveOnly() {
        List<HostSpec> slaves = getHostByRole(slaveRole);
        return choseByRandom(slaves);
    }

    protected List<HostSpec> getHostByRole(String role) {
        List<HostSpec> slaves = hostMap.get(role);
        if (slaves == null || slaves.size() < 1)
            throw new HostNotExpectedException(String.format(NO_HOST_AVAILABLE, role));
        return slaves;
    }

    protected HostSpec loadBalancePickHost(List<HostSpec> hostSpecs) {
        // todo-lhj load-balance strategy implement
        return hostSpecs.iterator().next();
    }



    protected HostSpec choseByRandom(List<HostSpec> hostSpecs) {
        if (hostSpecs == null || hostSpecs.size() == 0)
            throw new HostNotExpectedException(String.format(NO_HOST_AVAILABLE, "random host"));
        return hostSpecs.get((int)(Math.random() * hostSpecs.size()));
    }

    protected HostSpec dalHintsRoute (DalHints dalHints) {
        RouteStrategyEnum strategyEnum = dalHints.getRouteStrategy();
        dalHints.cleanRouteStrategy();
        if (readStrategyEnum.equals(strategyEnum))
            return this.pickRead(dalHints);

        if (routeStrategies.get(strategyEnum) == null) {
            synchronized (routeStrategies) {
                if (routeStrategies.get(strategyEnum) == null) {
                    try {
                        ReadStrategy tempRouteStrategy = (ReadStrategy)Class.forName(strategyEnum.getClazz()).newInstance();
                        tempRouteStrategy.init(hostSpecs, strategyProperties);
                        routeStrategies.put(strategyEnum, tempRouteStrategy);
                    } catch (Throwable e) {

                    }
                }
            }
        }

        return routeStrategies.get(strategyEnum).pickRead(dalHints);
    }

    @Override
    public void dispose() {

    }

    @Override
    public SQLException interceptException(SQLException sqlEx, HostConnection conn) {
        return null;
    }
}
