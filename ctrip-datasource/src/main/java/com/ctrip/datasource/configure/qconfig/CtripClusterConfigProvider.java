package com.ctrip.datasource.configure.qconfig;

import com.ctrip.datasource.configure.DynamicClusterConfig;
import com.ctrip.framework.dal.cluster.client.config.ClusterConfig;
import com.ctrip.framework.dal.cluster.client.config.ClusterConfigParser;
import com.ctrip.framework.dal.cluster.client.config.ClusterConfigXMLParser;
import com.ctrip.platform.dal.dao.configure.ClusterConfigProvider;
import qunar.tc.qconfig.client.Feature;
import qunar.tc.qconfig.client.TypedConfig;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author c7ch23en
 */
public class CtripClusterConfigProvider implements ClusterConfigProvider {

    private static final String CONFIG_GROUP = "100020718";

    private final ClusterConfigParser configParser;
    private final Map<String, ClusterConfig> configCache = new ConcurrentHashMap<>();

    public CtripClusterConfigProvider() {
        this(new ClusterConfigXMLParser());
    }

    public CtripClusterConfigProvider(ClusterConfigParser configParser) {
        this.configParser = configParser;
    }

    @Override
    public ClusterConfig getClusterConfig(String clusterName) {
        ClusterConfig config = configCache.get(clusterName);
        if (config == null) {
            synchronized (configCache) {
                config = configCache.get(clusterName);
                if (config == null) {
                    config = new DynamicClusterConfig(loadRawConfig(clusterName));
                    configCache.put(clusterName, config);
                }
            }
        }
        return config;
    }

    private TypedConfig<ClusterConfig> loadRawConfig(String clusterName) {
        Feature feature = Feature.create().setHttpsEnable(true).build();
        return TypedConfig.get(CONFIG_GROUP, clusterName, feature, new TypedConfig.Parser<ClusterConfig>() {
            @Override
            public ClusterConfig parse(String content) throws IOException {
                return configParser.parse(content);
            }
        });
    }

}
