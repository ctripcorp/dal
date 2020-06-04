package com.ctrip.datasource.configure.qconfig;

import com.ctrip.datasource.configure.DynamicClusterConfig;
import com.ctrip.framework.dal.cluster.client.config.ClusterConfig;
import com.ctrip.framework.dal.cluster.client.config.ClusterConfigParser;
import com.ctrip.framework.dal.cluster.client.config.ClusterConfigXMLParser;
import com.ctrip.platform.dal.dao.configure.AbstractClusterConfigProvider;
import com.ctrip.platform.dal.dao.configure.ClusterConfigProvider;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import qunar.tc.qconfig.client.Feature;
import qunar.tc.qconfig.client.TypedConfig;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author c7ch23en
 */
public class CtripClusterConfigProvider extends AbstractClusterConfigProvider implements ClusterConfigProvider {

    private static final String CONFIG_GROUP = "100020718";
    private static final String CAT_LOG_TYPE = "DAL.configure";
    private static final String CAT_LOG_NAME_FORMAT = "Cluster::getClusterConfig:%s";
    private static final String CAT_LOG_NAME_FORMAT2 = "Cluster::parseClusterConfig:%s";

    private static final Map<String, ClusterConfig> configCache = new ConcurrentHashMap<>();

    public CtripClusterConfigProvider() {
        super();
    }

    public CtripClusterConfigProvider(ClusterConfigParser configParser) {
        super(configParser);
    }

    @Override
    public ClusterConfig getClusterConfig(String clusterName) {
        String name = clusterName.toLowerCase();
        ClusterConfig config = configCache.get(name);
        if (config == null) {
            synchronized (configCache) {
                config = configCache.get(name);
                if (config == null) {
                    config = new DynamicClusterConfig(loadRawConfig(clusterName));
                    configCache.put(name, config);
                }
            }
        }
        return config;
    }

    private TypedConfig<ClusterConfig> loadRawConfig(String clusterName) {
        Transaction transaction = Cat.newTransaction(CAT_LOG_TYPE, String.format(CAT_LOG_NAME_FORMAT, clusterName));
        try {
            Feature feature = Feature.create().setHttpsEnable(true).build();
            TypedConfig<ClusterConfig> config = TypedConfig.get(CONFIG_GROUP, clusterName, feature, new TypedConfig.Parser<ClusterConfig>() {
                @Override
                public ClusterConfig parse(String content) throws IOException {
                    Transaction transaction2 = Cat.newTransaction(CAT_LOG_TYPE, String.format(CAT_LOG_NAME_FORMAT2, clusterName));
                    try {
                        ClusterConfig config2 = getParser().parse(content);
                        transaction2.addData(config2.toString());
                        transaction2.setStatus(Transaction.SUCCESS);
                        return config2;
                    } catch (Throwable t2) {
                        Cat.logError("Parse cluster config error, cluster name: " + clusterName, t2);
                        transaction2.setStatus(t2);
                        throw t2;
                    } finally {
                        transaction2.complete();
                    }
                }
            });
            transaction.setStatus(Transaction.SUCCESS);
            return config;
        } catch (Throwable t) {
            Cat.logError("Get cluster config error, cluster name: " + clusterName, t);
            transaction.setStatus(t);
            throw t;
        } finally {
            transaction.complete();
        }
    }

}
