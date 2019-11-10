package com.ctrip.framework.db.cluster.utils;

import com.ctrip.framework.foundation.Env;
import com.ctrip.framework.foundation.Foundation;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by taochen on 2019/11/7.
 */
public class EnvUtil {
    private static String ENV = null;
    public static String getEnv() {
        if (StringUtils.isEmpty(ENV)) {
            Env envEntity = Foundation.server().getEnv();
            ENV = envEntity.name().toLowerCase();
        }
        return ENV;
    }
}
