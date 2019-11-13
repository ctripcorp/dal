package com.ctrip.framework.db.cluster.enums;

public enum ClusterExtensionConfigType {

    shards_strategies(0, "shardStrategies"),
    id_generators(1, "idGenerators"),
    ;

    ClusterExtensionConfigType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static ClusterExtensionConfigType getType(final Integer code) {
        for (ClusterExtensionConfigType type : ClusterExtensionConfigType.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }

        throw new IllegalArgumentException(
                String.format("ClusterExtensionConfigType can't be match, code parameter is %s.", code)
        );
    }

    public static ClusterExtensionConfigType getType(final String name) {
        for (ClusterExtensionConfigType type : ClusterExtensionConfigType.values()) {
            if (type.getName().equals(name)) {
                return type;
            }
        }

        throw new IllegalArgumentException(
                String.format("ClusterExtensionConfigType can't be match, name parameter is %s.", name)
        );
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
