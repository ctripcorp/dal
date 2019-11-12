package com.ctrip.framework.dal.dbconfig.plugin.entity.mongo;

/**
 * Created by shenjie on 2019/4/3.
 */
public class Node {

    private String host;
    private Integer port;

    public Node(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "Node{" +
                "host='" + host + '\'' +
                ", port=" + port +
                '}';
    }
}
