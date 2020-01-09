package com.ctrip.platform.dal.dao.helper;

import java.lang.reflect.Field;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SqlTypeHelper {
    private volatile static SqlTypeHelper helper = null;
    private volatile static Map<Integer, String> map = new ConcurrentHashMap<>();

    public synchronized static SqlTypeHelper getInstance() {
        if (helper == null) {
            helper = new SqlTypeHelper();
            try {
                map = getAllJdbcTypeNames();
            } catch (Throwable e) {
            }
        }
        return helper;
    }

    public static Map<Integer, String> getAllJdbcTypeNames() throws Exception {
        Map<Integer, String> result = new HashMap<>();
        try {
            for (Field field : Types.class.getFields()) {
                result.put((Integer) field.get(null), field.getName());
            }
        } catch (Throwable e) {
        }
        return result;
    }

    public String getSqlTypeName(int sqlType) {
        return map.get(sqlType);
    }

}