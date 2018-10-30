package com.ctrip.platform.dal.dao.helper;

import com.ctrip.platform.dal.dao.configure.ErrorCodeInfo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ErrorCodesUtil {
    private static final Object LOCK = new Object();
    private volatile static ErrorCodesUtil util = null;
    private static final String SEPARATOR_SEMICOLON = ";";
    private static final String SEPARATOR_COMMA = ",";
    private static final int DEFAULT_CLEAR_INTERVAL_IN_SECONDS = 5;
    private static Set<Integer> HARD_CODED_READONLY_ERROR_CODES = null;

    private ErrorCodesUtil() {}

    public static ErrorCodesUtil getInstance() {
        if (util == null) {
            synchronized (LOCK) {
                if (util == null) {
                    util = new ErrorCodesUtil();
                    HARD_CODED_READONLY_ERROR_CODES = new HashSet<>();
                    HARD_CODED_READONLY_ERROR_CODES.add(3906);
                }
            }
        }

        return util;
    }

    public Map<Integer, ErrorCodeInfo> convertErrorCodes(String codes) {
        Map<Integer, ErrorCodeInfo> map = new HashMap<>();
        if (codes == null || codes.isEmpty()) {
            map = getDefaultParameters();
            return map;
        }

        String[] array = codes.split(SEPARATOR_SEMICOLON);
        for (String item : array) {
            String[] temp = item.split(SEPARATOR_COMMA);
            if (temp.length == 1) {
                map = getErrorCodeParameters(temp);
            } else if (temp.length == 2) {
                map = getFullParameters(temp);
            }
        }

        if (map.size() == 0) {
            map = getDefaultParameters();
        }

        return map;
    }

    private Map<Integer, ErrorCodeInfo> getDefaultParameters() {
        Map<Integer, ErrorCodeInfo> map = new HashMap<>();

        for (Integer item : HARD_CODED_READONLY_ERROR_CODES) {
            if (!map.containsKey(item)) {
                ErrorCodeInfo info = new ErrorCodeInfo();
                info.setErrorCode(item);
                info.setIntervalInSeconds(DEFAULT_CLEAR_INTERVAL_IN_SECONDS);
                map.put(item, info);
            }
        }

        return map;
    }

    private Map<Integer, ErrorCodeInfo> getErrorCodeParameters(String[] array) {
        Map<Integer, ErrorCodeInfo> map = new HashMap<>();
        int errorCode = -1;
        boolean result = true;
        try {
            errorCode = Integer.parseInt(array[0]);
        } catch (Throwable e) {
            result = false;
        }

        if (!result)
            return map;

        if (!map.containsKey(errorCode)) {
            ErrorCodeInfo info = new ErrorCodeInfo();
            info.setErrorCode(errorCode);
            info.setIntervalInSeconds(DEFAULT_CLEAR_INTERVAL_IN_SECONDS);
            map.put(errorCode, info);
        }

        return map;
    }

    private Map<Integer, ErrorCodeInfo> getFullParameters(String[] array) {
        Map<Integer, ErrorCodeInfo> map = new HashMap<>();
        int errorCode = -1;
        boolean result = true;
        try {
            errorCode = Integer.parseInt(array[0]);
        } catch (Throwable e) {
            result = false;
        }

        if (!result)
            return map;

        if (!map.containsKey(errorCode)) {
            ErrorCodeInfo info = new ErrorCodeInfo();
            info.setErrorCode(errorCode);
            info.setIntervalInSeconds(DEFAULT_CLEAR_INTERVAL_IN_SECONDS);

            map.put(errorCode, info);
        }

        int interval = -1;
        boolean success = true;
        try {
            interval = Integer.parseInt(array[1]);
        } catch (Throwable e) {
            success = false;
        }

        if (!success)
            return map;

        if (map.containsKey(errorCode)) {
            map.get(errorCode).setIntervalInSeconds(interval);
        }

        return map;
    }

    public Boolean errorCodesEquals(Map<Integer, ErrorCodeInfo> map1, Map<Integer, ErrorCodeInfo> map2) {
        if (map1 == null && map2 == null)
            return true;

        if (map1 == null || map2 == null)
            return false;

        Boolean result = map1.size() == map2.size() && map1.equals(map2); // TODO:confirm
        return result;
    }

}
