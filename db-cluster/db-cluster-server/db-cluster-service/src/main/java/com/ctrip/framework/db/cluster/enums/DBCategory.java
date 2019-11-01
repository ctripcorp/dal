package com.ctrip.framework.db.cluster.enums;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by shenjie on 2019/3/12.
 */
public enum DBCategory {

    MySql(1), SqlServer(2);

    private static final ConcurrentMap<Integer, DBCategory> codes = new ConcurrentHashMap<>();

    static {
        for (DBCategory type : DBCategory.values()) {
            codes.put(type.code, type);
        }
    }

    int code;

    DBCategory(int code) {
        this.code = code;
    }

    public static DBCategory codeOf(int code) {
        return codes.get(code);
    }

    public int getCode() {
        return code;
    }
}
