package com.ctrip.framework.dal.cluster.client.config;

import com.ctrip.framework.dal.cluster.client.base.ComponentOrdered;
import com.ctrip.framework.dal.cluster.client.sharding.idgen.ClusterIdGeneratorConfig;

import java.io.InputStream;

public interface IdGeneratorConfigParser extends ComponentOrdered {

    ClusterIdGeneratorConfig parse(String clusterName, String content);

    ClusterIdGeneratorConfig parse(String clusterName, InputStream stream);

}
