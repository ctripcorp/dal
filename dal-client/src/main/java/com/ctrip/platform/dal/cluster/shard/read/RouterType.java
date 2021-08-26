package com.ctrip.platform.dal.cluster.shard.read;

public enum RouterType {

    //todo-lhj 将哪个版本支持标明，并设计路由策略怎么支持指定

    SLAVE_ONLY("slave-only"), // the dataSource only provide slave connections
    MASTER_ONLY("master-only"),
    MASTER_SLAVES("master-slaves");


    public static RouterType getRouterType(String type) {
        if (type.equalsIgnoreCase("load-balance") || type.equalsIgnoreCase("slave-only")) {
            return SLAVE_ONLY;
        } else if (type.equalsIgnoreCase("master-only") || type.equalsIgnoreCase("fail-over")) {
            return MASTER_ONLY;
        } else {
            return MASTER_SLAVES;
        }
    }

    private String type;

    private RouterType(String type) {
        this.type = type;
    }

    public String getRouterType() {
        return type;
    }
}
