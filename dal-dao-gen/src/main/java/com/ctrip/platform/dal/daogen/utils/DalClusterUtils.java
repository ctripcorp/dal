package com.ctrip.platform.dal.daogen.utils;

import com.ctrip.platform.dal.daogen.enums.DbModeTypeEnum;

public class DalClusterUtils {

    public static String getModeTypeByDbBaseName( String baseName) {
        if (baseName != null && baseName.length() > 11 && DbModeTypeEnum.Cluster.getDes().equals(baseName.substring(baseName.length() - 10))) {
            return DbModeTypeEnum.Cluster.getDes();
        } else {
            return DbModeTypeEnum.Titan.getDes();
        }
    }
}
