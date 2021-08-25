package com.ctrip.platform.dal.dao.datasource.cluster.strategy;

import com.ctrip.platform.dal.cluster.base.HostSpec;
import com.ctrip.platform.dal.cluster.util.CaseInsensitiveProperties;
import com.ctrip.platform.dal.dao.datasource.cluster.ConnectionFactory;
import com.ctrip.platform.dal.dao.datasource.cluster.ShardMeta;
import com.ctrip.platform.dal.dao.datasource.cluster.validator.HostValidator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author limingdong
 * @create 2021/8/18
 */
public class ZoneDividedStrategyContext extends HashMap<String, Set<HostSpec>> implements StrategyContext {

    private ShardMeta shardMeta;

    private ConnectionFactory connFactory;

    private CaseInsensitiveProperties strategyProperties;

    private HostValidator hostValidator;

    public ZoneDividedStrategyContext(ShardMeta shardMeta, ConnectionFactory connFactory,
                                      CaseInsensitiveProperties strategyProperties, HostValidator hostValidator) {
        this.shardMeta = shardMeta;
        this.connFactory = connFactory;
        this.strategyProperties = strategyProperties;
        this.hostValidator = hostValidator;
    }

    public int shardIndex() {
        return shardMeta.shardIndex();
    }

    public String clusterName() {
        return shardMeta.clusterName();
    }

    public ConnectionFactory getConnFactory() {
        return connFactory;
    }

    public CaseInsensitiveProperties getStrategyProperties() {
        return strategyProperties;
    }

    public HostValidator getHostValidator() {
        return hostValidator;
    }

    @Override
    public MultiHostStrategy accept(StrategyTransformer transformer) {
        Set<HostSpec> allHostSpecs = shardMeta.configuredHosts();

        for (HostSpec hostSpec : allHostSpecs) {
            String zone = hostSpec.zone();
            Set<HostSpec> hostSpecSet = get(zone);
            if (hostSpecSet == null) {
                hostSpecSet = new HashSet<>();
                put(zone, hostSpecSet);
            }
            hostSpecSet.add(hostSpec);
        }

        return transformer.visit(this);
    }
}
