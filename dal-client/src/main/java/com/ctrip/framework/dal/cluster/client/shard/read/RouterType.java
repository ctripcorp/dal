package com.ctrip.framework.dal.cluster.client.shard.read;

public enum RouterType {

    READ_ONLY("read-only"),
    WRITE_ONLY("write-only"),
    READ_WRITE("read-write");


    public static RouterType getRouterType(String type) {
        if (READ_ONLY.type.equalsIgnoreCase(type)) {
            return READ_ONLY;
        } else if (WRITE_ONLY.type.equalsIgnoreCase(type)) {
            return WRITE_ONLY;
        } else {
            return READ_WRITE;
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
