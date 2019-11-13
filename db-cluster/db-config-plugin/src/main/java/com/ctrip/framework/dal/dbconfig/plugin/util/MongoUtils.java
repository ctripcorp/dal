package com.ctrip.framework.dal.dbconfig.plugin.util;


import org.apache.commons.lang3.StringUtils;

/**
 * Created by shenjie on 2019/4/4.
 */
public class MongoUtils {

    public static String formatClusterName(String clusterName) {
        //to lowercase
        if (StringUtils.isNotBlank(clusterName)) {
            clusterName = clusterName.toLowerCase();
        }
        return clusterName;
    }

}
