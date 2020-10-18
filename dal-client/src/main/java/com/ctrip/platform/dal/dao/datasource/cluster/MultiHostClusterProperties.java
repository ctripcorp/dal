package com.ctrip.platform.dal.dao.datasource.cluster;

import java.util.Properties;

/**
 * @author c7ch23en
 */
public interface MultiHostClusterProperties {

    String routeStrategyName();

    Properties routeStrategyProperties();

}
