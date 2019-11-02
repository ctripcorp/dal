package com.ctrip.framework.db.cluster.util;

import com.ctrip.framework.foundation.Foundation;

/**
 * Created by shenjie on 2019/3/6.
 */
public class Constants {

    public static final String CONFIG_FILE_NAME = "db-cluster-service.properties";

    public static final String TITAN_PLUGIN_APPID = "100010061";
    public static final String MONGO_CLIENT_APPID = "100019648";
    public static final String DAL_CLUSTER_SERVICE_APPID = "100020032";

    public static final String TITAN_KEY_NAME = "fxdalclusterdb_w";
    public static final String DATABASE_SET_NAME = "dalclusterdemodb_w";

    public static final String USER_TAG_DEFAULT = "default";
    public static final String USER_TAG_ETL = "etl";

    public static final String ROLE_MASTER = "master";
    public static final String ROLE_CANDIDATE_MASTER = "candidate_master";
    public static final String ROLE_SLAVE = "slave";
    public static final String ROLE_READ = "read";

    public static final String MYSQL_PROVIDER_NAME = "MySql.Data.MySqlClient";
    public static final String SQL_SERVER_PROVIDER_NAME = "System.Data.SqlClient";

    public static final String OPERATION_WRITE = "write";
    public static final String OPERATION_READ = "read";

    public static final String ENV_PRO = "pro";
    public static final String ENV_FAT = "fat";
    public static final String ENV_UAT = "uat";
    public static final String ENV = Foundation.server().getEnvFamily().getName();

    public static final String MYSQL_DB = "mysql";
    public static final String SQL_SERVER_DB = "sqlServer";

    public static final String POINT_SEPARATOR = ".";

    public static final int DEFAULT_TIMEOUT_MS = 10000;
}
