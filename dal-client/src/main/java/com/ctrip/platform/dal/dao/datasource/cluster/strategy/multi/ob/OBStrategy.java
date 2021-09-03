package com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.ob;

import com.ctrip.platform.dal.cluster.base.HostSpec;
import com.ctrip.platform.dal.cluster.exception.HostNotExpectedException;
import com.ctrip.platform.dal.cluster.util.CaseInsensitiveProperties;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.ConnectionFactoryAware;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.AbstractMultiMasterStrategy;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.MultiMasterStrategy;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.validator.HostValidator;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.validator.SimpleHostValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @Author limingdong
 * @create 2021/8/25
 */
public class OBStrategy extends AbstractMultiMasterStrategy implements MultiMasterStrategy, ConnectionFactoryAware {

    private CompositeRoundRobinStrategy delegate;

    @Override
    public void init(Set<HostSpec> hostSpecs, CaseInsensitiveProperties strategyProperties) {
        super.init(hostSpecs, strategyProperties);
        ZoneDividedStrategyContext strategyContext = new ZoneDividedStrategyContext(hostSpecs, strategyProperties, hostValidator);
        delegate = (CompositeRoundRobinStrategy) strategyContext.accept(new CompositeStrategyTransformer()); // start validator to monitor shardMeta in all zone instead of monitoring every zone in ValidatorAwareRoundRobinAccessStrategy to reducing thread resources
    }

    @Override
    protected void doBuildOrderHosts() {
        this.orderHosts = new ArrayList<>(configuredHosts);
    }

    @Override
    protected HostValidator newHostValidator(Set<HostSpec> configuredHosts, List<HostSpec> orderHosts, long failOverTime, long blackListTimeOut, long fixedValidatePeriod) {
        return new SimpleHostValidator(configuredHosts, orderHosts, failOverTime, blackListTimeOut, fixedValidatePeriod);
    }

    @Override
    public HostSpec pickNode(DalHints hints) throws HostNotExpectedException {
        return delegate.pickNode(hints);
    }

    // for test
    protected void setZone(String zone) {
        delegate.setZone(zone);
    }
}