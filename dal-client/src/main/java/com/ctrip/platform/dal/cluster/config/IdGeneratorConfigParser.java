package com.ctrip.platform.dal.cluster.config;

import com.ctrip.platform.dal.cluster.base.ComponentOrdered;
import com.ctrip.platform.dal.cluster.sharding.idgen.ClusterIdGeneratorConfig;

import java.io.InputStream;

public interface IdGeneratorConfigParser extends ComponentOrdered {

    ClusterIdGeneratorConfig parse(String clusterName, String content);

    ClusterIdGeneratorConfig parse(String clusterName, InputStream stream);

}
