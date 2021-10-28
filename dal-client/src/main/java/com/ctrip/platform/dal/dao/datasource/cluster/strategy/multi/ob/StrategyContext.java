package com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.ob;

import com.ctrip.platform.dal.dao.datasource.cluster.strategy.RouteStrategy;

/**
 * @Author limingdong
 * @create 2021/8/18
 */
public interface StrategyContext {

    RouteStrategy accept(StrategyTransformer transformer);
}
