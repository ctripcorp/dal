package com.ctrip.platform.dal.dao.configure;

import com.ctrip.framework.dal.cluster.client.config.ClusterConfig;
import com.ctrip.framework.dal.cluster.client.config.ClusterConfigParser;
import com.ctrip.framework.dal.cluster.client.exception.ClusterConfigException;

import java.io.InputStream;
import java.net.URL;

/**
 * @author c7ch23en
 */
public class LocalClusterConfigProvider extends AbstractClusterConfigProvider implements ClusterConfigProvider {

    public LocalClusterConfigProvider() {}

    public LocalClusterConfigProvider(ClusterConfigParser parser) {
        super(parser);
    }

    @Override
    public ClusterConfig getClusterConfig(String clusterName) {
        try {
            String fileName = getFileName(clusterName);
            URL url = LocalClusterConfigProvider.class.getClassLoader().getResource(fileName);
            InputStream is = url.openStream();
            return getParser().parse(is);
        } catch (Throwable t) {
            throw new ClusterConfigException(t);
        }
    }

    protected String getFileName(String clusterName) {
        return clusterName + ".xml";
    }

}
