package com.ctrip.platform.dal.dao.datasource.cluster.strategy;

import com.ctrip.framework.dal.cluster.client.base.HostSpec;
import com.ctrip.platform.dal.dao.datasource.cluster.ShardMeta;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;

import java.util.Map;
import java.util.Set;

/**
 * @Author limingdong
 * @create 2021/8/18
 */
public class CompositeStrategyTransformer implements StrategyTransformer {

    @Override
    public MultiHostStrategy visit(StrategyContext strategyContext) {
        try {
            if (strategyContext instanceof ZoneDividedStrategyContext) {
                ZoneDividedStrategyContext obStrategyGenerator = (ZoneDividedStrategyContext) strategyContext;
                CompositeRoundRobinAccessStrategy localizedAccessStrategy = new CompositeRoundRobinAccessStrategy();

                for (Map.Entry<String, Set<HostSpec>> entry : obStrategyGenerator.entrySet()) {
                    String zone = entry.getKey();
                    Set<HostSpec> hostSpecSet = entry.getValue();
                    ValidatorAwareRoundRobinAccessStrategy strategy = new ValidatorAwareRoundRobinAccessStrategy();
                    strategy.initialize(getShardMeta(obStrategyGenerator.shardIndex(),
                            hostSpecSet,
                            obStrategyGenerator.clusterName()),
                            obStrategyGenerator.getConnFactory(),
                            obStrategyGenerator.getStrategyProperties());
                    strategy.setHostValidator(obStrategyGenerator.getHostValidator());

                    localizedAccessStrategy.put(zone, strategy);
                }

                return localizedAccessStrategy;
            }
        } catch (Exception e) {
            throw new DalRuntimeException("generate error", e);
        }

        throw new DalRuntimeException("StrategyContext mismatch for " + strategyContext.getClass());
    }

    private ShardMeta getShardMeta(int shardIndex, Set<HostSpec> hostSpecSet, String clusterName) {
        return new ShardMeta() {
            @Override
            public int shardIndex() {
                return shardIndex;
            }

            @Override
            public Set<HostSpec> configuredHosts() {
                return hostSpecSet;
            }

            @Override
            public String clusterName() {
                return clusterName;
            }
        };
    }
}
