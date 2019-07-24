package com.ctrip.framework.dal.dbconfig.plugin.constant;

/**
 * @author c7ch23en
 */
public interface TitanConstants extends CommonConstants {

    String TITAN_QCONFIG_PLUGIN_APPID = "100009917";
    String TITAN_QCONFIG_KEYS_APPID = "100010061";
    String TITAN_QCONFIG_PLUGIN_CONFIG_FILE = "titan_qconfig_plugin.properties";
    String TITAN_QCONFIG_PLUGIN_IGNITE_FILE = "plugin_ignite.properties";
    String TITAN_QCONFIG_CONTENT_LINE_SPLITTER = "\n";
    String DB_INDEX_DELIMITER = "\n";
    String BEGIN_TIME = "beginTime";
    String KEY_TRAIL_SH = "_SH";

    String REQ_PARAM_TITAN_KEY = "titankey";

    String REQ_ATTR_TITAN_KEY = "attr_titankey";
    String REQ_ATTR_TITAN_RUN_IN_BIGDATA = "attr_titan_run_in_bigData";
    String REQ_ATTR_TITAN_ENV_LIST = "attr_titan_env_list";
    String REQ_ATTR_TITAN_CLIENT_APPID = "attr_titan_client_appid";

    String KEYSERVICE_SOA_URL = "key.service.soa.url";
    String SSLCODE = "sslCode";
    String TITAN_ADMIN_SERVER_LIST = "titan.admin.server.list";
    String NEED_CHECK_DB_CONNECTION = "needCheckDbConnection";
    String NEED_CHECK_DB_CONNECTION_FOR_MHA = "needCheckDbConnectionForMHA";
    String DBA_CONNECTION_CHECK_URL = "dba.connection.check.url";

    String HTTP_WHITE_LIST = "http.white.list";
    String TOKEN_KEY = "token.key";
    String HTTP_READ_TIMEOUT_MS = "http.read.timeout.ms";
    String INDEX_FILE_UPDATE_ENABLED = "index.file.update.enabled";
    String INDEX_ENABLED = "index.enabled";
    String INDEX_DBNAME_KEY_SHARD_NUM = "index.dbName.key.shard.num";
    String INDEX_DBNAME_KEY_SHARD_PREFIX = "index.dbName.key.shard.prefix";
    String INDEX_DBNAME_SHARD_NUM = "index.dbName.shard.num";
    String INDEX_DBNAME_SHARD_PREFIX = "index.dbName.shard.prefix";
    String PERMISSION_PRO_SUBENV_LIST = "permission.pro.subenv.list";
    String PERMISSION_VALID_ENABLED = "permission.valid.enabled";
    String PERMISSION_VALID_FREE_IPLIST = "permission.valid.free.ipList";
    String PERMISSION_VALID_FREE_APPIDLIST = "permission.valid.free.appIdList";
    String PERMISSION_VALID_SKIP_BLANK_APPID_ALLOWED = "permission.valid.skip.blank.appId.allowed";
    String PERMISSION_VALID_DETECT_ENABLED = "permission.valid.detect.enabled";
    String APPID_IP_CHECK_ENABLED = "appId.ip.check.enabled";
    String APPID_IP_CHECK_FREE_APPIDLIST = "appId.ip.check.free.appIdList";
    public static final String APPID_IP_CHECK_SERVICE_URL = "appId.ip.check.service.url";
    public static final String APPID_IP_CHECK_SERVICE_TOKEN = "appId.ip.check.service.token";
    String CMS_GET_GROUP_SERVICE_URL = "cms.get.group.service.url";
    String CMS_ACCESS_TOKEN = "cms.access.token";
    String APPID_IP_CHECK_SERVICE_PASS_CODELIST = "appId.ip.check.service.pass.codeList";
    String APPID_IP_CHECK_HTTP_READ_TIMEOUT_MS = "appId.ip.check.http.read.timeout.ms";
    String MHA_LAST_UPDATE_ALLOW_INTERVAL_MIN = "mha.last.update.allow.interval.min";

    //titan - key file
    String WHITE_LIST = "whiteList";
    String BLACK_LIST = "blackList";
    String ENABLED = "enabled";
    String TIMEOUT = "timeOut";
    String VERSION = "version";
    String CREATE_USER = "createUser";
    String UPDATE_USER = "updateUser";
    String ID = "id";
    String PERMISSIONS = "permissions";
    String FREE_VERIFY_IPLIST = "freeVerifyIpList";
    String FREE_VERIFY_APPID_LIST = "freeVerifyAppIdList";
    String MHA_LAST_UPDATE_TIME = "mhaLastUpdateTime";

    String CONNECTIONSTRING_PROVIDER_NAME = "providerName";
    String CONNECTIONSTRING_SERVER_NAME = "serverName";
    String CONNECTIONSTRING_SERVER_IP = "serverIp";
    String CONNECTIONSTRING_PORT = "port";
    String CONNECTIONSTRING_UID = "uid";
    String CONNECTIONSTRING_PASSWORD = "password";
    String CONNECTIONSTRING_DB_NAME = "dbName";
    String CONNECTIONSTRING_EXT_PARAM = "extParam";
    String CONNECTIONSTRING_KEY_NAME = "keyName";
    String PASSWORD_HIDDEN = "*****";

    //db
    String FORMAT_SQLSERVER_CONNECTIONSTRING = "Server=%s,%s;UID=%s;password=%s;database=%s;";
    String FORMAT_MYSQL_CONNECTIONSTRING = "Server=%s;port=%s;UID=%s;password=%s;database=%s;";
    String NAME_SQLSERVER_PROVIDER = "System.Data.SqlClient";
    String NAME_MYSQL_PROVIDER = "MySql.Data.MySqlClient";
    String NAME_SQLSERVER = "sqlserver";
    String NAME_MYSQL = "mysql";

    // net type
    String HEADER_NET_TYPE = "net-type";
    String PUBLIC_NET_TYPE = "public";
    String PRIVATE_NET_TYPE = "private";

}
