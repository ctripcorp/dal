package com.ctrip.framework.db.cluster.enums;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by shenjie on 2019/3/12.
 */
public enum OperateType {

    WRITE(1), READ(2);

    private static final ConcurrentMap<Integer, OperateType> codes = new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, OperateType> names = new ConcurrentHashMap<>();

    static {
        for (OperateType type : OperateType.values()) {
            codes.put(type.code, type);
        }

        for (OperateType type : OperateType.values()) {
            names.put(type.name(), type);
        }
    }

    int code;

    OperateType(int code) {
        this.code = code;
    }

    public static OperateType codeOf(int code) {
        return codes.get(code);
    }

    public static OperateType nameOf(String name) {
        return names.get(name);
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name();
    }

}
