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
    String REQ_ATTR_DB_NAME = "attr_dbName";

    String REQUEST_SCHEMA_HTTPS = "https";
    String ENV_PRO = "PRO";
    String ENV_UAT = "UAT";
    String ENV_FAT = "FAT";
    String TT_TOKEN = "tt-token";
    String X_REAL_IP = "X-Real-IP";
    String NO_PARENT_SUFFIX = "no.parent.suffix";

    int PAAS_RETURN_CODE_SUCCESS = 0;
    int PAAS_RETURN_CODE_FAIL_INNER = 4;
    int PAAS_RETURN_CODE_NOT_MATCH = 1;

    //titan - plugin_ignite.properties
    String IGNITE_PREWARM_CACHE_ENABLED = "ignite.prewarm.cache.enabled";
    String IGNITE_PREWARM_CACHE_APPIDS = "ignite.prewarm.cache.appIds";
    String CACHE_NORMAL_REFRESH_INTERVAL_MIN = "cache.normal.refresh.interval.min";
    String CMS_GET_APP_URL = "cms.get.app.url";
    String APPID_IP_CHECK_BATCH_FETCH_COUNT = "appId.ip.check.batch.fetch.count";

}
