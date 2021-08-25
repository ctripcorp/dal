package com.ctrip.platform.dal.dao.configure;


import com.ctrip.platform.dal.cluster.config.ClusterConfigParser;
import com.ctrip.platform.dal.cluster.config.ClusterConfigXMLParser;

/**
 * @author c7ch23en
 */
public abstract class AbstractClusterConfigProvider implements ClusterConfigProvider {

    private ClusterConfigParser parser;

    public AbstractClusterConfigProvider() {
        this(new ClusterConfigXMLParser());
    }

    public AbstractClusterConfigProvider(ClusterConfigParser parser) {
        this.parser = parser;
    }

    protected ClusterConfigParser getParser() {
        return parser;
    }

}
