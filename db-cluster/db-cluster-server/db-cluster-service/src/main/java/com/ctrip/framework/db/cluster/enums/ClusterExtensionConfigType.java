package com.ctrip.framework.db.cluster.enums;

public enum ClusterExtensionConfigType {

    shards_strategies(0, "ShardStrategies"),
    id_generators(1, "IdGenerators"),
    ;

    ClusterExtensionConfigType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    private int code;

    private String name;


    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
