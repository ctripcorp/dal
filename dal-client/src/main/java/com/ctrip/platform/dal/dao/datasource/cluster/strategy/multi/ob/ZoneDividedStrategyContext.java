package com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.ob;

import com.ctrip.platform.dal.cluster.base.HostSpec;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.RouteStrategy;
import com.ctrip.platform.dal.cluster.util.CaseInsensitiveProperties;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.validator.HostValidator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author limingdong
 * @create 2021/8/18
 */
public class ZoneDividedStrategyContext extends HashMap<String, Set<HostSpec>> implements StrategyContext {

    private Set<HostSpec> hostSpecs;

    private CaseInsensitiveProperties strategyProperties;

    private HostValidator hostValidator;

    // for unit test
    private String zone;

    public ZoneDividedStrategyContext(Set<HostSpec> hostSpecs, CaseInsensitiveProperties strategyProperties, HostValidator hostValidator) {
        this(hostSpecs, strategyProperties, hostValidator, null);
    }

    public ZoneDividedStrategyContext(Set<HostSpec> hostSpecs, CaseInsensitiveProperties strategyProperties, HostValidator hostValidator, String zone) {
        this.hostSpecs = hostSpecs;
        this.strategyProperties = strategyProperties;
        this.hostValidator = hostValidator;
        this.zone = zone;
    }

    public Set<HostSpec> getHostSpecs() {
        return hostSpecs;
    }

    public CaseInsensitiveProperties getStrategyProperties() {
        return strategyProperties;
    }

    public HostValidator getHostValidator() {
        return hostValidator;
    }

    public String getZone() {
        return zone;
    }

    @Override
    public RouteStrategy accept(StrategyTransformer transformer) {
        for (HostSpec hostSpec : hostSpecs) {
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
