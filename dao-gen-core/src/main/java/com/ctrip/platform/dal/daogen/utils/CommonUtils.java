package com.ctrip.platform.dal.daogen.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

public class CommonUtils {
    private static ObjectMapper objectMap;
    private static Logger log;

    static {
        objectMap = new ObjectMapper();
        log = Logger.getLogger(CommonUtils.class);
    }

    public static String normalizeVariable(String variable) {
        return variable.replaceAll("[^A-Za-z0-9()\\[\\]_]", "");
    }

    public static int tryParse(String val, int defaultValue) {
        int v = defaultValue;
        try {
            v = Integer.parseInt(val);
        } catch (Throwable e) {
        }
        return v;
    }

    public static String toJson(Object o) throws Exception {
        String res = "";
        try {
            res = objectMap.writeValueAsString(o);
        } catch (Throwable e) {
            throw e;
        }
        return res;
    }
}
