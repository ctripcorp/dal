package com.ctrip.framework.dal.cluster.client.config;

import com.ctrip.framework.dal.cluster.client.sharding.idgen.ClusterIdGeneratorConfig;
import org.w3c.dom.Node;

public interface IdGeneratorConfigXMLParser {

    ClusterIdGeneratorConfig parse(String clusterName, Node xmlNode);

}
