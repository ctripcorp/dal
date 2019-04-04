package com.ctrip.framework.dal.dbconfig.plugin.constant;

/**
 * @author c7ch23en
 */
public interface CommonConstants {

    String REQ_PARAM_ENV = "env";
    String REQ_PARAM_SUB_ENV = "subenv";
    String REQ_PARAM_OPERATOR = "operator";
    String REQ_PARAM_TARGET_APPID = "appid";
    String REQ_PARAM_CLUSTER_NAME = "clustername";

    String REQ_ATTR_ENV_PROFILE = "attr_env_profile";
    String REQ_ATTR_OPERATOR = "attr_operator";
    String REQ_ATTR_CLUSTER_NAME = "attr_clustername";

    String REQUEST_SCHEMA_HTTPS = "https";
    String ENV_PRO = "PRO";
    String ENV_UAT = "UAT";
    String ENV_FAT = "FAT";
    String TT_TOKEN = "tt-token";
    String X_REAL_IP = "X-Real-IP";
    String NO_PARENT_SUFFIX = "no.parent.suffix";

    public static final int PAAS_RETURN_CODE_SUCCESS = 0;
    public static final int PAAS_RETURN_CODE_FAIL_INNER = 4;

    public static final String APPID_IP_CHECK_REQUEST_TEMPLATE = "{\"app_id\": \"%s\", \"env\": \"%s\", \"ip\": \"%s\"}";


    //titan - plugin_ignite.properties
    public static final String IGNITE_PREWARM_CACHE_ENABLED = "ignite.prewarm.cache.enabled";
    public static final String IGNITE_PREWARM_CACHE_APPIDS = "ignite.prewarm.cache.appIds";
    public static final String CACHE_NORMAL_REFRESH_INTERVAL_MIN = "cache.normal.refresh.interval.min";
    public static final String APPID_IP_CHECK_FETCH_ALL_APPID_URL = "appId.ip.check.fetch.all.appId.url";
    public static final String APPID_IP_CHECK_BATCH_FETCH_RELATION_URL = "appId.ip.check.batch.fetch.relation.url";
    public static final String APPID_IP_CHECK_BATCH_FETCH_COUNT = "appId.ip.check.batch.fetch.count";

}
