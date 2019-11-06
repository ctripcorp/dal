package com.ctrip.framework.db.cluster.schedule;

import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

/**
 * Created by @author zhuYongMing on 2019/11/3.
 */
public class FreshnessScheduleRegistration {

    private static FreshnessScheduleRegistration registration;

    private final Set<String> targetClusters;


    private FreshnessScheduleRegistration() {
        this.targetClusters = Sets.newCopyOnWriteArraySet();
    }

    public static FreshnessScheduleRegistration getRegistration() {
        if (registration == null) {
            synchronized (FreshnessScheduleRegistration.class) {
                if (registration == null) {
                    registration = new FreshnessScheduleRegistration();
                }
            }
        }
        return registration;
    }

    public void registryToSchedule(final List<String> clusterNames) {
        targetClusters.addAll(clusterNames);
    }

    public void removeFromSchedule(final String clusterName) {
        targetClusters.remove(clusterName);
    }

    public Set<String> getTargetClusters() {
        return targetClusters;
    }
}
