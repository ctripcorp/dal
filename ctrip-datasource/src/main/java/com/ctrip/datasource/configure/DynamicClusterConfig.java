package com.ctrip.datasource.configure;

import com.ctrip.framework.dal.cluster.client.Cluster;
import com.ctrip.framework.dal.cluster.client.base.ListenableSupport;
import com.ctrip.framework.dal.cluster.client.base.Listener;
import com.ctrip.framework.dal.cluster.client.config.ClusterConfig;
import qunar.tc.qconfig.client.Configuration;
import qunar.tc.qconfig.client.TypedConfig;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author c7ch23en
 */
public class DynamicClusterConfig extends ListenableSupport<ClusterConfig> implements ClusterConfig {

    private TypedConfig<ClusterConfig> rawConfig;
    private AtomicReference<ClusterConfig> configRef = new AtomicReference<>();

    public DynamicClusterConfig(TypedConfig<ClusterConfig> rawConfig) {
        this.rawConfig = rawConfig;
        init();
    }

    @Override
    public Cluster generateCluster() {
        return getConfig().generateCluster();
    }

    private void init() {
        configRef.set(rawConfig.current());
        rawConfig.addListener(new Configuration.ConfigListener<ClusterConfig>() {
            @Override
            public void onLoad(ClusterConfig current) {
                for (Listener<ClusterConfig> listener : getListeners()) {
                    listener.onChanged(current);
                }
            }
        });
    }

    private ClusterConfig getConfig() {
        return configRef.get();
    }

}
