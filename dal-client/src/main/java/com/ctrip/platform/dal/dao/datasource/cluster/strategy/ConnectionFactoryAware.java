package com.ctrip.platform.dal.dao.datasource.cluster.strategy;

import com.ctrip.platform.dal.dao.datasource.cluster.ConnectionFactory;

/**
 * @Author limingdong
 * @create 2021/8/25
 */
public interface ConnectionFactoryAware {

    void setConnectionFactory(ConnectionFactory connectionFactory);
}
