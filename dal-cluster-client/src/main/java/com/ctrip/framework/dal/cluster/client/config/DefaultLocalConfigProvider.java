package com.ctrip.framework.dal.cluster.client.config;

import com.ctrip.framework.dal.cluster.client.exception.ClusterConfigException;

import java.io.InputStream;
import java.net.URL;

/**
 * @author c7ch23en
 */
public class DefaultLocalConfigProvider implements ClusterConfigProvider {

    private String fileName;
    private ClusterConfigParser parser = new ClusterConfigXMLParser();

    public DefaultLocalConfigProvider(String clusterName) {
        this.fileName = clusterName + ".xml";
    }

    @Override
    public ClusterConfig getClusterConfig() {
        try {
            URL url = DefaultLocalConfigProvider.class.getClassLoader().getResource(fileName);
            InputStream is = url.openStream();
            return parser.parse(is);
        } catch (Throwable t) {
            throw new ClusterConfigException(t);
        }
    }

}
