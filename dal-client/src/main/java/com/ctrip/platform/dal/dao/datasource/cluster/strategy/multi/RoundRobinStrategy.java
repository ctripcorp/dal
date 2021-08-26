package com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi;

import com.ctrip.platform.dal.cluster.base.HostSpec;
import com.ctrip.platform.dal.cluster.exception.HostNotExpectedException;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.RouteStrategy;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.validator.HostValidator;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.validator.SimpleHostValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author limingdong
 * @create 2021/8/18
 */
public class RoundRobinStrategy extends AbstractMultiHostStrategy implements RouteStrategy {

    private AtomicInteger index = new AtomicInteger(0);

    private int hostsSize;

    @Override
    protected void doBuildOrderHosts() {
        this.orderHosts = new ArrayList<>(configuredHosts);
        this.hostsSize = orderHosts.size();
    }

    @Override
    protected HostValidator newHostValidator(Set<HostSpec> configuredHosts, List<HostSpec> orderHosts, long failOverTime, long blackListTimeOut, long fixedValidatePeriod) {
        return new SimpleHostValidator(configuredHosts, orderHosts, failOverTime, blackListTimeOut, fixedValidatePeriod);
    }

    private int getIndex() {
        index.compareAndSet(hostsSize, 0);
        int currentIndex = Math.abs(index.getAndIncrement());
        return currentIndex % hostsSize;
    }

    @Override
    public HostSpec pickNode(DalHints hints) throws HostNotExpectedException {
        for (int i = 0; i < hostsSize; ++i) {
            HostSpec hostSpec = orderHosts.get(getIndex());
            if (hostValidator.available(hostSpec)) {
                return hostSpec;
            }
        }

        throw new HostNotExpectedException(String.format(NO_HOST_AVAILABLE, orderHosts.toString()));
    }

}
