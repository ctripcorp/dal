package com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.ob;

import com.ctrip.framework.dal.cluster.client.base.HostSpec;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.RouteStrategy;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.validator.HostValidatorAware;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;

import java.util.Map;
import java.util.Set;

/**
 * @Author limingdong
 * @create 2021/8/18
 */
public class CompositeStrategyTransformer implements StrategyTransformer {

    @Override
    public RouteStrategy visit(StrategyContext strategyContext) {
        try {
            if (strategyContext instanceof ZoneDividedStrategyContext) {
                ZoneDividedStrategyContext obStrategyContext = (ZoneDividedStrategyContext) strategyContext;
                CompositeRoundRobinStrategy localizedAccessStrategy = new CompositeRoundRobinStrategy();
                if (obStrategyContext.getZone() != null) {
                    localizedAccessStrategy.setZone(obStrategyContext.getZone().toUpperCase());
                }

                for (Map.Entry<String, Set<HostSpec>> entry : obStrategyContext.entrySet()) {
                    String zone = entry.getKey();
                    Set<HostSpec> hostSpecSet = entry.getValue();
                    RouteStrategy strategy = new ValidatorAwareRoundRobinStrategy();
                    strategy.init(hostSpecSet, obStrategyContext.getStrategyProperties());
                    if (strategy instanceof HostValidatorAware) {
                        ((HostValidatorAware) strategy).setHostValidator(obStrategyContext.getHostValidator());
                    }

                    localizedAccessStrategy.put(zone.toUpperCase(), strategy);
                }

                return localizedAccessStrategy;
            }
        } catch (Exception e) {
            throw new DalRuntimeException("generate error", e);
        }

        throw new DalRuntimeException("StrategyContext mismatch for " + strategyContext.getClass());
    }

}
