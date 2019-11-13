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
    public Cluster generate() {
        return getConfig().generate();
    }

    @Override
    public boolean checkSwitchable(ClusterConfig newConfig) {
        return false;
    }

    private void init() {
        configRef.set(rawConfig.current());
        rawConfig.addListener(new Configuration.ConfigListener<ClusterConfig>() {
            @Override
            public void onLoad(ClusterConfig current) {
                if (getConfig().checkSwitchable(current)) {
                    configRef.getAndSet(current);
                    for (Listener<ClusterConfig> listener : getListeners()) {
                        try {
                            listener.onChanged(DynamicClusterConfig.this);
                        } catch (Throwable t) {
                            // ignore
                        }
                    }
                }
            }
        });
    }

    private ClusterConfig getConfig() {
        return configRef.get();
    }

}
