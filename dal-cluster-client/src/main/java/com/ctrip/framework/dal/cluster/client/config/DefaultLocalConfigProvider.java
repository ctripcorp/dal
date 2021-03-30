package com.ctrip.framework.dal.cluster.client.config;

import com.ctrip.framework.dal.cluster.client.database.DatabaseRole;
import com.ctrip.framework.dal.cluster.client.exception.ClusterConfigException;
import com.ctrip.framework.dal.cluster.client.util.FileUtils;

import java.io.InputStream;
import java.net.URL;

/**
 * @author c7ch23en
 */
public class DefaultLocalConfigProvider implements ClusterConfigProvider {

    private final String clusterName;
    private final ClusterConfigParser parser;

    public DefaultLocalConfigProvider(String clusterName) {
        this(clusterName, new ClusterConfigXMLParser());
    }

    public DefaultLocalConfigProvider(String clusterName, ClusterConfigParser parser) {
        this.clusterName = clusterName;
        this.parser = parser;
    }

    @Override
    public ClusterConfig getClusterConfig() {
        try (InputStream is = FileUtils.getResourceInputStream(getConfigFileName(),
                DefaultLocalConfigProvider.class.getClassLoader())) {
            return parser.parse(is, new DalConfigCustomizedOption() {
                @Override
                public String getConsistencyTypeCustomizedClass() {
                    return null;
                }

                @Override
                public boolean isIgnoreShardingResourceNotFound() {
                    return false;
                }

                @Override
                public boolean isForceInitialize() {
                    return false;
                }

                @Override
                public Integer getShardIndex() {
                    return null;
                }

                @Override
                public DatabaseRole getDatabaseRole() {
                    return null;
                }

                @Override
                public DalConfigCustomizedOption clone() {
                    return null;
                }
            });
        } catch (Throwable t) {
            throw new ClusterConfigException("Load cluster config failed, cluster name: " + clusterName, t);
        }
    }

    @Override
    public ClusterConfig getClusterConfig(DalConfigCustomizedOption customizedOption) {
        try (InputStream is = FileUtils.getResourceInputStream(getConfigFileName(),
                DefaultLocalConfigProvider.class.getClassLoader())) {
            return parser.parse(is, customizedOption);
        } catch (Throwable t) {
            throw new ClusterConfigException("Load cluster config failed, cluster name: " + clusterName, t);
        }
    }

    protected String getConfigFileName() {
        return clusterName != null ? clusterName + ".xml" : null;
    }

}
