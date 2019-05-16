package com.ctrip.framework.dal.dbconfig.plugin.util;

/**
 * Created by lzyan on 2017/8/22.
 */
public class CommonConstant {

    //other
    public static final String TITAN_QCONFIG_PLUGIN_APPID = "100009917";
    public static final String TITAN_QCONFIG_KEYS_APPID = "100010061";
    public static final String TITAN_QCONFIG_PLUGIN_CONFIG_FILE = "titan_qconfig_plugin.properties";
    public static final String TITAN_QCONFIG_PLUGIN_IGNITE_FILE = "plugin_ignite.properties";
    public static final String TITAN_QCONFIG_CONTENT_LINE_SPLITTER = "\n";
    public static final String REQUEST_SCHEMA_HTTPS = "https";
    public static final String ENV_PRO = "PRO";
    public static final String ENV_UAT = "UAT";
    public static final String ENV_FAT = "FAT";
    public static final String TT_TOKEN = "tt-token";
    public static final String X_REAL_IP = "X-Real-IP";
    public static final String BEGIN_TIME = "beginTime";
    public static final String DB_INDEX_DELIMITER = "\n";
    public static final String KEY_TRAIL_SH = "_SH";
    public static final String APPID_IP_CHECK_REQUEST_TEMPLATE = "{\"app_id\": \"%s\", \"env\": \"%s\", \"ip\": \"%s\"}";
    public static final int PAAS_RETURN_CODE_SUCCESS = 0;
    public static final int PAAS_RETURN_CODE_FAIL_INNER = 4;


    //titan - self config file
    public static final String KEYSERVICE_SOA_URL = "key.service.soa.url";
    public static final String SSLCODE = "sslCode";
    public static final String TITAN_ADMIN_SERVER_LIST = "titan.admin.server.list";
    public static final String NEED_CHECK_DB_CONNECTION = "needCheckDbConnection";
    public static final String NEED_CHECK_DB_CONNECTION_FOR_MHA = "needCheckDbConnectionForMHA";
    public static final String DBA_CONNECTION_CHECK_URL = "dba.connection.check.url";
    public static final String NO_PARENT_SUFFIX = "no.parent.suffix";
    public static final String HTTP_WHITE_LIST = "http.white.list";
    public static final String TOKEN_KEY = "token.key";
    public static final String HTTP_READ_TIMEOUT_MS = "http.read.timeout.ms";
    public static final String INDEX_FILE_UPDATE_ENABLED = "index.file.update.enabled";
    public static final String INDEX_ENABLED = "index.enabled";
    public static final String INDEX_DBNAME_KEY_SHARD_NUM = "index.dbName.key.shard.num";
    public static final String INDEX_DBNAME_KEY_SHARD_PREFIX = "index.dbName.key.shard.prefix";
    public static final String INDEX_DBNAME_SHARD_NUM = "index.dbName.shard.num";
    public static final String INDEX_DBNAME_SHARD_PREFIX = "index.dbName.shard.prefix";
    public static final String PERMISSION_PRO_SUBENV_LIST = "permission.pro.subenv.list";
    public static final String PERMISSION_VALID_ENABLED = "permission.valid.enabled";
    public static final String PERMISSION_VALID_FREE_IPLIST = "permission.valid.free.ipList";
    public static final String PERMISSION_VALID_FREE_APPIDLIST = "permission.valid.free.appIdList";
    public static final String PERMISSION_VALID_SKIP_BLANK_APPID_ALLOWED = "permission.valid.skip.blank.appId.allowed";
    public static final String PERMISSION_VALID_DETECT_ENABLED = "permission.valid.detect.enabled";
    public static final String APPID_IP_CHECK_ENABLED = "appId.ip.check.enabled";
    public static final String APPID_IP_CHECK_FREE_APPIDLIST = "appId.ip.check.free.appIdList";
    public static final String APPID_IP_CHECK_SERVICE_URL = "appId.ip.check.service.url";
    public static final String APPID_IP_CHECK_SERVICE_TOKEN = "appId.ip.check.service.token";
    public static final String APPID_IP_CHECK_SERVICE_PASS_CODELIST = "appId.ip.check.service.pass.codeList";
    public static final String APPID_IP_CHECK_HTTP_READ_TIMEOUT_MS = "appId.ip.check.http.read.timeout.ms";
    public static final String MHA_LAST_UPDATE_ALLOW_INTERVAL_MIN = "mha.last.update.allow.interval.min";

    //titan - plugin_ignite.properties
    public static final String IGNITE_PREWARM_CACHE_ENABLED = "ignite.prewarm.cache.enabled";
    public static final String IGNITE_PREWARM_CACHE_APPIDS = "ignite.prewarm.cache.appIds";
    public static final String CACHE_NORMAL_REFRESH_INTERVAL_MIN = "cache.normal.refresh.interval.min";
    public static final String APPID_IP_CHECK_FETCH_ALL_APPID_URL = "appId.ip.check.fetch.all.appId.url";
    public static final String APPID_IP_CHECK_BATCH_FETCH_RELATION_URL = "appId.ip.check.batch.fetch.relation.url";
    public static final String APPID_IP_CHECK_BATCH_FETCH_COUNT = "appId.ip.check.batch.fetch.count";


    //titan - key file
    public static final String WHITE_LIST = "whiteList";
    public static final String BLACK_LIST = "blackList";
    public static final String ENABLED = "enabled";
    public static final String TIMEOUT = "timeOut";
    public static final String VERSION = "version";
    public static final String CREATE_USER = "createUser";
    public static final String UPDATE_USER = "updateUser";
    public static final String ID = "id";
    public static final String PERMISSIONS = "permissions";
    public static final String FREE_VERIFY_IPLIST = "freeVerifyIpList";
    public static final String FREE_VERIFY_APPID_LIST = "freeVerifyAppIdList";
    public static final String MHA_LAST_UPDATE_TIME = "mhaLastUpdateTime";


    public static final String CONNECTIONSTRING_PROVIDER_NAME = "providerName";
    public static final String CONNECTIONSTRING_SERVER_NAME = "serverName";
    public static final String CONNECTIONSTRING_SERVER_IP = "serverIp";
    public static final String CONNECTIONSTRING_PORT = "port";
    public static final String CONNECTIONSTRING_UID = "uid";
    public static final String CONNECTIONSTRING_PASSWORD = "password";
    public static final String CONNECTIONSTRING_DB_NAME = "dbName";
    public static final String CONNECTIONSTRING_EXT_PARAM = "extParam";
    public static final String CONNECTIONSTRING_KEY_NAME = "keyName";
    public static final String PASSWORD_HIDDEN = "*****";


    //cat
    public static final String OPERATION_TYPE_POST_SITE = "Post_Site";
    public static final String OPERATION_TYPE_POST_MHA = "Post_MHA";
    public static final String OPERATION_TYPE_GET_SITE = "Get_Site";
    public static final String OPERATION_TYPE_GET_QCONFIG = "Get_QConfig";
    public static final String OPERATION_TYPE_LIST_SITE = "List_Site";


    //parameter
    public static final String PARAM_TARGET_APPID = "appid";
    public static final String PARAM_ENV = "env";
    public static final String PARAM_SUBENV = "subenv";
    public static final String PARAM_TITAN_KEY = "titankey";

    //attribute
    public static final String ATTRIBUTE_TITAN_PROFILE = "attr_titan_profile";
    public static final String ATTRIBUTE_TITAN_KEY = "attr_titan_titankey";
    public static final String ATTRIBUTE_TITAN_CLIENT_APPID = "attr_titan_client_appid";
    public static final String ATTRIBUTE_TITAN_DBNAME = "attr_titan_dbName";
    public static final String ATTRIBUTE_TITAN_RUN_IN_BIGDATA = "attr_titan_run_in_bigData";
    public static final String ATTRIBUTE_TITAN_ENV_LIST = "attr_titan_env_list";

    //db
    public static final String FORMAT_SQLSERVER_CONNECTIONSTRING = "Server=%s,%s;UID=%s;password=%s;database=%s;";
    public static final String FORMAT_MYSQL_CONNECTIONSTRING = "Server=%s;port=%s;UID=%s;password=%s;database=%s;";
    public static final String NAME_SQLSERVER_PROVIDER = "System.Data.SqlClient";
    public static final String NAME_MYSQL_PROVIDER = "MySql.Data.MySqlClient";
    public static final String NAME_SQLSERVER = "sqlserver";
    public static final String NAME_MYSQL = "mysql";

    //Cat
    public static final String E_TITAN_KEY_MHA_UPDATE_PLUGIN_TITANKEY_UPDATE = "TitanKeyMHAUpdatePlugin.TitanKey.Update";


}
