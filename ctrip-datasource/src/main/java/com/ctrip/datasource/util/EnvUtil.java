package com.ctrip.datasource.util;

import com.ctrip.framework.foundation.Env;
import com.ctrip.framework.foundation.Foundation;
import org.apache.commons.lang3.StringUtils;

public class EnvUtil {
    private static String ENV = null;
    private static String IDC = null;

    public static String getEnv() {
        if (StringUtils.isEmpty(ENV)) {
            Env envEntity = Foundation.server().getEnv();
            ENV = envEntity.name().toLowerCase();
        }
        return ENV;
    }

    public static String getIdc() {
        if (StringUtils.isEmpty(IDC)) {
            IDC = Foundation.server().getDataCenter();
        }
        return IDC;
    }

    public static void setEnv(String env) {
        ENV = env;
    }
}
