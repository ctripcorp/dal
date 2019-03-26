package com.ctrip.framework.db.cluster.util;

import com.ctrip.framework.db.cluster.config.ConfigManager;
import org.apache.commons.lang.StringUtils;

import java.util.Set;


/**
 * Created by shenjie on 2019/3/19.
 */
public class ValidityChecker {

    public static boolean checkAllowedIp(String ip) {
        if (StringUtils.isEmpty(ip)) {
            return false;
        }

        Set<String> allowedIps = ConfigManager.getInstance().getAllowedIps();
        if (allowedIps == null) {
            return false;
        }

        return allowedIps.contains(ip);
    }
}
