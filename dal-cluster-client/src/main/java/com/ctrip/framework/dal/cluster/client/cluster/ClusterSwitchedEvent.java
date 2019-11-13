package com.ctrip.framework.dal.cluster.client.cluster;

import com.ctrip.framework.dal.cluster.client.Cluster;

public class ClusterSwitchedEvent {

    private Cluster current;
    private Cluster previous;

    public ClusterSwitchedEvent(Cluster current, Cluster previous) {
        this.current = current;
        this.previous = previous;
    }

    public Cluster getCurrent() {
        return current;
    }

    public Cluster getPrevious() {
        return previous;
    }

}
