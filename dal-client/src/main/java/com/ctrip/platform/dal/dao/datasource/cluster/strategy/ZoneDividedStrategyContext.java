package com.ctrip.platform.dal.dao.datasource.cluster.strategy;

import com.ctrip.framework.dal.cluster.client.base.HostSpec;
import com.ctrip.framework.dal.cluster.client.util.CaseInsensitiveProperties;
import com.ctrip.platform.dal.dao.datasource.cluster.ConnectionFactory;
import com.ctrip.platform.dal.dao.datasource.cluster.ShardMeta;

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

    public ZoneDividedStrategyContext(ShardMeta shardMeta, ConnectionFactory connFactory, CaseInsensitiveProperties strategyProperties) {
        this.shardMeta = shardMeta;
        this.connFactory = connFactory;
        this.strategyProperties = strategyProperties;
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
