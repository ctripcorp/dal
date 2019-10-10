package com.ctrip.platform.dal.dao.helper;

import java.sql.Connection;
import java.sql.DatabaseMetaData;

/**
 * Created by taochen on 2019/10/10.
 */
public class ConnectionHelper {
    public static String obtainUrl(Connection connection) {
        String url = "";
        if (connection != null) {
            try {
                DatabaseMetaData metaData = connection.getMetaData();
                if (metaData != null) {
                    url = LoggerHelper.getSimplifiedDBUrl(metaData.getURL());
                }
            } catch (Throwable e) {
                //ignore
            }
        }
        return url;
    }
}
