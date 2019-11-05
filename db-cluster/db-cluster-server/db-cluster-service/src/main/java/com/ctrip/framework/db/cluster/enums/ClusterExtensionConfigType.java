package com.ctrip.framework.db.cluster.enums;

public enum ClusterExtensionConfigType {

    shards_strategies(0, "ShardStrategies"),
    id_generators(1, "IdGenerators"),
    ;

    ClusterExtensionConfigType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static Integer getTypeCode(final String name) {
        for (ClusterExtensionConfigType type : ClusterExtensionConfigType.values()) {
            if (type.getName().equals(name)) {
                return type.getCode();
            }
        }

        throw new IllegalArgumentException(
                String.format("ClusterExtensionConfigType can't be match, name parameter is %s.", name)
        );
    }

    public static String getTypeName(final Integer code) {
        for (ClusterExtensionConfigType type : ClusterExtensionConfigType.values()) {
            if (type.getCode() == code) {
                return type.getName();
            }
        }

        throw new IllegalArgumentException(
                String.format("ClusterExtensionConfigType can't be match, code parameter is %s.", code)
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
