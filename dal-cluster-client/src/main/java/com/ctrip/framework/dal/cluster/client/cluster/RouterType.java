package com.ctrip.framework.dal.cluster.client.cluster;

public enum RouterType {

    //todo-lhj 将哪个版本支持标明，并设计路由策略怎么支持指定

    SLAVE_ONLY("slave-only"), 		// new version > 2.8.3
    MASTER_ONLY("master-only"), 	// new version > 2.8.3
    ROUTE_STRATEGY("route-strategy");


    public static RouterType getRouterType(String type) {
        if (type.equalsIgnoreCase("load-balance") || type.equalsIgnoreCase("slave-only")) {
            return SLAVE_ONLY;
        } else if (type.equalsIgnoreCase("master-only") || type.equalsIgnoreCase("fail-over")) {
            return MASTER_ONLY;
        } else {
            return ROUTE_STRATEGY;
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
