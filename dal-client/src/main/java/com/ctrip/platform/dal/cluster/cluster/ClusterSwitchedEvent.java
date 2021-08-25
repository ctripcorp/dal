package com.ctrip.platform.dal.cluster.cluster;

import com.ctrip.platform.dal.cluster.Cluster;

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
