package com.ctrip.framework.db.cluster.entity;

import java.util.List;

/**
 * Created by taochen on 2019/11/6.
 */
public class ShardInstance {
    private String domain;

    private int port;

    private Instance instance;

    private List<Instance> instances;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Instance getInstance() {
        return instance;
    }

    public void setInstance(Instance instance) {
        this.instance = instance;
    }

    public List<Instance> getInstances() {
        return instances;
    }

    public void setInstances(List<Instance> instances) {
        this.instances = instances;
    }
}
