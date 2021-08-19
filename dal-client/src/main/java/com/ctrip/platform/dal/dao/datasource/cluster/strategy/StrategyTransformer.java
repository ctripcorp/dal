package com.ctrip.platform.dal.dao.datasource.cluster.strategy;


/**
 * @Author limingdong
 * @create 2021/8/18
 */
public interface StrategyTransformer {

    MultiHostStrategy visit(StrategyContext strategyContext);
}
