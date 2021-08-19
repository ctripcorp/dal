package com.ctrip.platform.dal.dao.datasource.cluster.strategy;

import com.ctrip.framework.dal.cluster.client.base.HostSpec;
import com.ctrip.framework.dal.cluster.client.cluster.ClusterType;
import com.ctrip.framework.dal.cluster.client.util.CaseInsensitiveProperties;
import com.ctrip.platform.dal.dao.datasource.cluster.ConnectionFactory;
import com.ctrip.platform.dal.dao.datasource.cluster.ShardMeta;
import com.ctrip.platform.dal.exceptions.DalException;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author limingdong
 * @create 2021/8/18
 */
public class RoundRobinAccessStrategy extends AbstractMultiHostStrategy implements MultiHostStrategy {

    private AtomicInteger index = new AtomicInteger(0);

    private int hostsSize;

    @Override
    public void initialize(ShardMeta shardMeta, ConnectionFactory connFactory, CaseInsensitiveProperties strategyProperties) {
        super.initialize(shardMeta, connFactory, strategyProperties);
        this.hostsSize = orderHosts.size();
    }

    @Override
    protected void doBuildOrderHosts () {
        this.orderHosts = new ArrayList<>(configuredHosts);
    }

    /**
     * try next when network jitter
     * @return
     */
    @Override
    protected boolean networkFailFast() {
        return true;
    }

    protected String getCatLogType() {
        return "DAL." + ClusterType.OB.getValue();
    }

    @Override
    protected HostSpec pickHost() throws DalException {
        for (int i = 0; i < hostsSize; ++i) {
            int currentIndex = getIndex();
            HostSpec hostSpec = orderHosts.get(currentIndex % hostsSize);
            if (hostValidator.available(hostSpec)) {
                return hostSpec;
            }
        }

        throw new DalException(String.format(NO_HOST_AVAILABLE, orderHosts.toString()));
    }

    private int getIndex() {
        int currentIndex = index.getAndAdd(1);
        if (currentIndex < 0) {
            currentIndex = 0;
            index.set(1);
        }
        return currentIndex;
    }

}
