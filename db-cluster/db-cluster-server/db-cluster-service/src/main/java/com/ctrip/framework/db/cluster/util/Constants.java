package com.ctrip.framework.db.cluster.util;

import com.ctrip.framework.foundation.Foundation;

/**
 * Created by shenjie on 2019/3/6.
 */
public class Constants {

    // config name
    public static final String CONFIG_FILE_NAME = "db-cluster-service.properties";

    // appId
    public static final String TITAN_PLUGIN_APPID = "100010061";
    public static final String MONGO_CLIENT_APPID = "100019648";
    public static final String DAL_CLUSTER_SERVICE_APPID = "100020032";

    // titan key name
    public static final String TITAN_KEY_NAME = "fxdalclusterdb_w";

    // logic db name
    public static final String DATABASE_SET_NAME = "dalclusterdemodb_w";

    // user tags
    public static final String USER_TAG_DEFAULT = "default";
    public static final String USER_TAG_ETL = "etl";

    // role
    public static final String ROLE_MASTER = "master";
    public static final String ROLE_CANDIDATE_MASTER = "candidate_master";
    public static final String ROLE_SLAVE = "slave";
    public static final String ROLE_READ = "read";

    // db driver name
    public static final String MYSQL_PROVIDER_NAME = "MySql.Data.MySqlClient";
    public static final String SQL_SERVER_PROVIDER_NAME = "System.Data.SqlClient";

    // operation type
    public static final String OPERATION_WRITE = "write";
    public static final String OPERATION_READ = "read";

    // env
    public static final String ENV_PRO = "pro";
    public static final String ENV_FAT = "fat";
    public static final String ENV_UAT = "uat";
    public static final String ENV_FWS = "fws";
    public static final String ENV = getEnv();

    // db category
    public static final String MYSQL_DB = "mysql";
    public static final String SQL_SERVER_DB = "sqlServer";

    // release types
    public static final String RELEASE_TYPE_NORMAL_RELEASE = "normal_release";
    public static final String RELEASE_TYPE_SWITCH_RELEASE = "switch_release";
    public static final String RELEASE_TYPE_TRANSFORM_RELEASE = "transform_release";
    public static final String RELEASE_TYPE_HEALTH_SCHEDULE_RELEASE = "health_schedule_release";

    // timeout
    public static final int DEFAULT_TIMEOUT_MS = 10000;

    // operator
    public static final String HEALTH_SCHEDULE_OPERATOR = "dal-cluster-health-schedule";


    // other
    public static final String POINT_SEPARATOR = ".";

    private static String getEnv() {
        final String actualEnv = Foundation.server().getEnv().getName();
        return ENV_FWS.equalsIgnoreCase(actualEnv) ? ENV_FAT : actualEnv;
    }
}