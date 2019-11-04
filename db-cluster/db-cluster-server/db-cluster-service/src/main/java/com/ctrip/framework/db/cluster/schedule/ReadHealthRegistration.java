package com.ctrip.framework.db.cluster.schedule;

import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

/**
 * Created by @author zhuYongMing on 2019/11/3.
 */
public class ReadHealthRegistration {

    private static ReadHealthRegistration registration;

    private final Set<String> targetClusters = Sets.newCopyOnWriteArraySet();

    private ReadHealthRegistration() {

    }

    public static ReadHealthRegistration getRegistration() {
        if (registration == null) {
            synchronized (ReadHealthRegistration.class) {
                if (registration == null) {
                    registration = new ReadHealthRegistration();
                }
            }
        }
        return registration;
    }

    public void registryToSchedule(final String clusterName) {
        targetClusters.add(clusterName);
    }

    public void registryToSchedule(final List<String> clusterNames) {
        targetClusters.addAll(clusterNames);
    }

    public Set<String> getTargetClusters() {
        return targetClusters;
    }
}
