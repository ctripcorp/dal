package com.ctrip.platform.dal.application.Config;

import com.ctrip.platform.dal.application.service.DALRequestTask;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import qunar.tc.qconfig.client.Configuration;
import qunar.tc.qconfig.client.MapConfig;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;


@Component
public class DalApplicationConfig {
    private static final String CONFIG_NAME = "dalapplication.properties";
    private Map<String, String> applicationConfig;
    private AtomicReference<Boolean> isListenerAdded = new AtomicReference<>(false);
    private AtomicReference<MapConfig> mapConfigReference = new AtomicReference<>();
    private AtomicReference<Boolean> isFirstTime = new AtomicReference<>(true);

    @Autowired
    private DALRequestTask dalRequestTask;

    @PostConstruct
    public void initMap() {
        MapConfig mapConfig = getMapConfig();
        applicationConfig = mapConfig.asMap();
        addListener(mapConfig);
    }

    private MapConfig getMapConfig() {
        MapConfig mapConfig = mapConfigReference.get();
        if (mapConfig == null) {
            mapConfig = MapConfig.get(CONFIG_NAME);
            mapConfigReference.set(mapConfig);
        }

        return mapConfig;
    }

    private void addListener(MapConfig mapConfig) {
        boolean isAdded = isListenerAdded.get().booleanValue();
        if (!isAdded) {
            addPropertiesChangedListener(mapConfig);
            isListenerAdded.compareAndSet(false, true);
        }
    }

    private void addPropertiesChangedListener(MapConfig mapConfig) {
        mapConfig.addListener(new Configuration.ConfigListener<Map<String, String>>() {
            @Override
            public void onLoad(Map<String, String> map) {
                Transaction transaction = Cat.newTransaction("DalApplication", "ConfigChanged");
                try {
                    if (map == null || map.isEmpty())
                        throw new RuntimeException("Config cannot be null");

                    String notifyQPS = map.get("QPS");
                    Cat.logEvent("DalApplication", "ConfigChanged",Message.SUCCESS,String.format("new qps is %s",notifyQPS));
                    if (notifyQPS == null || notifyQPS.isEmpty())
                        throw new RuntimeException("QPS cannot be null");

                    if(isFirstTime.getAndSet(false)){
                        Cat.logEvent("DalApplication", "ConfigChanged",Message.SUCCESS,String.format("first load"));
                        return;
                    }

                    dalRequestTask.restart();
                    Cat.logEvent("DalApplication", "ConfigChanged",Message.SUCCESS,String.format("task restart"));

                    transaction.setStatus(Transaction.SUCCESS);
                } catch (Throwable e) {
                    transaction.setStatus(e);
                    Cat.logError(e);
//                    throw e;
                    throw new RuntimeException("onLoad error", e);
                } finally {
                    transaction.complete();
                }
            }
        });
    }


    public String getQPS() {
        return applicationConfig != null ? applicationConfig.get("QPS") : null;
    }

    public String getClusterName() {
        return applicationConfig != null ? applicationConfig.get("clusterName") : null;
    }
}
