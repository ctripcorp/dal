package com.ctrip.platform.dal.dao.datasource.cluster;

/**
 * @author c7ch23en
 */
public interface RouteStrategyManager {

    RouteStrategyManager DEFAULT = new DefaultRouteStrategyManager();

    RouteStrategy getOrCreateRouteStrategy(String name);

}
