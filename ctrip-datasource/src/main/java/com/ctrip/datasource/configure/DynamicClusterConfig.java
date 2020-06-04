package com.ctrip.datasource.configure;

import com.ctrip.framework.dal.cluster.client.Cluster;
import com.ctrip.framework.dal.cluster.client.base.ListenableSupport;
import com.ctrip.framework.dal.cluster.client.base.Listener;
import com.ctrip.framework.dal.cluster.client.config.ClusterConfig;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.log.ILogger;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.dianping.cat.message.Transaction;
import qunar.tc.qconfig.client.Configuration;
import qunar.tc.qconfig.client.TypedConfig;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author c7ch23en
 */
public class DynamicClusterConfig extends ListenableSupport<ClusterConfig> implements ClusterConfig {

    private static final String CAT_LOG_TYPE = "DAL.configure";
    private static final String CAT_LOG_NAME_LOAD_FORMAT = "Cluster::loadClusterConfig:%s";
    private static final String CAT_LOG_NAME_SWITCH_FORMAT = "Cluster::switchClusterConfig:%s";

    private String clusterName;
    private TypedConfig<ClusterConfig> rawConfig;
    private AtomicReference<ClusterConfig> configRef = new AtomicReference<>();

    public DynamicClusterConfig(TypedConfig<ClusterConfig> rawConfig) {
        this.rawConfig = rawConfig;
        init();
    }

    @Override
    public String getClusterName() {
        return clusterName;
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
        clusterName = getConfig().getClusterName();
        rawConfig.addListener(new Configuration.ConfigListener<ClusterConfig>() {
            @Override
            public void onLoad(ClusterConfig current) {
                String tNameLoad = String.format(CAT_LOG_NAME_LOAD_FORMAT, clusterName);
                Transaction tLoad = Cat.newTransaction(CAT_LOG_TYPE, tNameLoad);
                try {
                    tLoad.addData(current.toString());
                    if (getConfig().checkSwitchable(current)) {
                        String tNameSwitch = String.format(CAT_LOG_NAME_SWITCH_FORMAT, clusterName);
                        Transaction tSwitch = Cat.newTransaction(CAT_LOG_TYPE, tNameSwitch);
                        try {
                            ClusterConfig previous = configRef.getAndSet(current);
                            Cat.logEvent(CAT_LOG_TYPE, tNameSwitch, Event.SUCCESS, "Previous config: " + previous.toString());
                            for (Listener<ClusterConfig> listener : getListeners()) {
                                try {
                                    listener.onChanged(DynamicClusterConfig.this);
                                } catch (Throwable t) {
                                    Cat.logEvent(CAT_LOG_TYPE, tNameSwitch, Event.SUCCESS, "ListenerError: " + listener.toString());
                                }
                            }
                            tSwitch.setStatus(Transaction.SUCCESS);
                        } catch (Throwable t2) {
                            tSwitch.setStatus(t2);
                            throw t2;
                        } finally {
                            tSwitch.complete();
                        }
                    } else {
                        Cat.logEvent(CAT_LOG_TYPE, tNameLoad, Event.SUCCESS, "no switch");
                    }
                    tLoad.setStatus(Transaction.SUCCESS);
                } catch (Throwable t1) {
                    tLoad.setStatus(t1);
                    throw t1;
                } finally {
                    tLoad.complete();
                }
            }
        });
    }

    private ClusterConfig getConfig() {
        return configRef.get();
    }

    @Override
    public String toString() {
        return getConfig().toString();
    }

}
