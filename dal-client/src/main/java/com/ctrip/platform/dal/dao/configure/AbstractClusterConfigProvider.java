package com.ctrip.platform.dal.dao.configure;


import com.ctrip.framework.dal.cluster.client.config.ClusterConfigParser;
import com.ctrip.framework.dal.cluster.client.config.ClusterConfigXMLParser;

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
