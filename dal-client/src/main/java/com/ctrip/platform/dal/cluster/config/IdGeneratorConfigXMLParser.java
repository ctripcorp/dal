package com.ctrip.platform.dal.cluster.config;

import com.ctrip.platform.dal.cluster.sharding.idgen.ClusterIdGeneratorConfig;
import org.w3c.dom.Node;

public interface IdGeneratorConfigXMLParser extends IdGeneratorConfigParser {

    ClusterIdGeneratorConfig parse(String clusterName, Node xmlNode);

}
