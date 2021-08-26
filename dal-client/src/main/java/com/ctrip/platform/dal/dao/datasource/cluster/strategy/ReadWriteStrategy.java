package com.ctrip.platform.dal.dao.datasource.cluster.strategy;

/**
 * @Author limingdong
 * @create 2021/8/26
 */
public interface ReadWriteStrategy extends ReadStrategy, WriteStrategy, RouteStrategy {

}
