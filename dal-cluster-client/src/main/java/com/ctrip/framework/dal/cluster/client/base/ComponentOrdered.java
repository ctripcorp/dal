package com.ctrip.framework.dal.cluster.client.base;

public interface ComponentOrdered {

    int HIGHEST_PRECEDENCE = Integer.MIN_VALUE;

    int LOWEST_PRECEDENCE = Integer.MAX_VALUE;

    int getOrder();

}
