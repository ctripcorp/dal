package com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.mgr;

import com.ctrip.framework.dal.cluster.client.base.HostSpec;
import com.ctrip.framework.dal.cluster.client.exception.HostNotExpectedException;
import com.ctrip.framework.dal.cluster.client.util.CaseInsensitiveProperties;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.datasource.cluster.ZonedHostSorter;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.HostConnectionValidatorHolder;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.AbstractMultiMasterStrategy;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.MultiMasterStrategy;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.validator.HostConnectionValidator;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.validator.HostValidator;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.validator.MajorityHostValidator;

import java.util.List;
import java.util.Set;

/**
 * @Author limingdong
 * @create 2021/8/25
 */
public class MGRStrategy extends AbstractMultiMasterStrategy implements MultiMasterStrategy, HostConnectionValidatorHolder {

    private static final String CONNECTION_HOST_CHANGE = "Router::connectionHostChange:%s";

    private static final String CHANGE_FROM_TO = "change from %s to %s";

    private volatile HostSpec currentHost;

    @Override
    public void init(Set<HostSpec> hostSpecs, CaseInsensitiveProperties strategyProperties) {
        super.init(hostSpecs, strategyProperties);
        this.currentHost = orderHosts.get(0);
    }

    @Override
    protected void doBuildOrderHosts() {
        List<String> zoneOrder = strategyOptions.getStringList(ZONES_PRIORITY, ",", null);
        ZonedHostSorter sorter = new ZonedHostSorter(zoneOrder);
        this.orderHosts = sorter.sort(configuredHosts);
    }

    @Override
    protected HostValidator newHostValidator(Set<HostSpec> configuredHosts, List<HostSpec> orderHosts, long failOverTime, long blackListTimeOut, long fixedValidatePeriod) {
        return new MajorityHostValidator(configuredHosts, orderHosts, failOverTime, blackListTimeOut, fixedValidatePeriod);
    }

    @Override
    public HostSpec pickNode(DalHints hints) throws HostNotExpectedException {
        for (HostSpec hostSpec : orderHosts) {
            if (hostValidator.available(hostSpec)) {
                onNodePick(hostSpec);
                return hostSpec;
            }
        }

        throw new HostNotExpectedException(String.format(NO_HOST_AVAILABLE, orderHosts.toString()));
    }

    private void onNodePick(HostSpec hostSpec) {
        synchronized (this) {
            if (!hostSpec.equals(currentHost)) {
                LOGGER.warn(String.format(CONNECTION_HOST_CHANGE, String.format(CHANGE_FROM_TO, currentHost.toString(), hostSpec.toString())));
                LOGGER.logEvent(CAT_LOG_TYPE, String.format(CONNECTION_HOST_CHANGE, cluster), String.format(CHANGE_FROM_TO, currentHost.toString(), hostSpec.toString()));
                currentHost = hostSpec;
            }
        }
    }

    @Override
    public HostConnectionValidator getHostConnectionValidator() {
        return hostValidator;
    }

}