package com.ctrip.platform.dal.catlog;

import com.ctrip.platform.dal.sql.logging.DalEventEnum;

/**
 * Created by wcyuan on 2015/1/30.
 */
public class CatInfo {
    public static String getTypeSQLInfo(DalEventEnum operation){
        switch (operation){
            case QUERY:
                return "SELECT";
            case BATCH_UPDATE:
            case BATCH_UPDATE_PARAM:
            case UPDATE_KH:
            case UPDATE_SIMPLE:
                return "UPDATE";

            case CALL:
            case BATCH_CALL:
                return "STORED_PROCEDURE";
            default:
                return "UNKNOWN";

        }
    }
}
