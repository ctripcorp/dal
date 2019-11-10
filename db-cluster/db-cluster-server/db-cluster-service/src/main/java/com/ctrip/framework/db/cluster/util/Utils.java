package com.ctrip.framework.db.cluster.util;

import com.ctrip.framework.db.cluster.enums.HealthStatus;
import com.google.gson.Gson;

/**
 * Created by shenjie on 2019/3/18.
 */
public class Utils {

    public static final Gson gson = new Gson();

    public static int getStatusCode(Boolean enable) {
        if (enable == null) {
            return HealthStatus.HEALTHY.getCode();
        }
        if (enable) {
            return HealthStatus.HEALTHY.getCode();
        } else {
            return HealthStatus.UNHEALTHY.getCode();
        }
    }

    public static boolean getEnabled(Integer statusCode) {
        if (statusCode == null) {
            return true;
        }
        return statusCode == HealthStatus.HEALTHY.getCode();
    }

    public static String format(String content) {
        return content.trim().toLowerCase();
    }
}
