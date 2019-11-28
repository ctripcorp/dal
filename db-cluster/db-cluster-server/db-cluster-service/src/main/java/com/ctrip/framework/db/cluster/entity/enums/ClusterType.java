package com.ctrip.framework.db.cluster.entity.enums;

/**
 * Created by zhuYongMing on 2019/11/19.
 */
public enum ClusterType {

    normal(0, "normal"),
    drc(1, "drc"),;

    public static ClusterType getType(final int code) {
        for (ClusterType type : ClusterType.values()) {
            if (code == type.getCode()) {
                return type;
            }
        }

        throw new IllegalArgumentException(
                String.format("ClusterType can't be match, code parameter is %s.", code)
        );
    }


    ClusterType(int code, String name) {
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
