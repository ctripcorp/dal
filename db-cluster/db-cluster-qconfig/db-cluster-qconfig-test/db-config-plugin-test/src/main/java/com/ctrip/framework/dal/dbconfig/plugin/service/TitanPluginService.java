package com.ctrip.framework.dal.dbconfig.plugin.service;

import com.ctrip.framework.dal.dbconfig.plugin.entity.*;
import com.ctrip.framework.dal.dbconfig.plugin.util.HttpUtils;
import com.ctrip.framework.dal.dbconfig.plugin.util.Utils;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by shenjie on 2019/4/9.
 */
@Slf4j
@Service
public class TitanPluginService {

    // qconfig fat16
//    public static final String TITAN_PLUGIN_URL = "http://qconfig.fat16.qa.nt.ctripcorp.com/plugins/titan";
//    public static final String TITAN_PLUGIN_URL = "http://qconfig.ctripcorp.com/plugins/titan";
    public static final String TITAN_PLUGIN_URL = "http://localhost:8082/plugins/titan";

    public static final String TITAN_PLUGIN_CONFIG_URL = TITAN_PLUGIN_URL + "/config";
    public static final String TITAN_PLUGIN_CONFIGS_URL = TITAN_PLUGIN_URL + "/configs";
    public static final String TITAN_PLUGIN_CONFIGS_BY_TIME_URL = TITAN_PLUGIN_URL + "/configs/bytime";
    public static final String TITAN_PLUGIN_CONFIG_MHA_URL = TITAN_PLUGIN_URL + "/config/mha";

    public static final String TITAN_PLUGIN_SSL_CODE_URL = TITAN_PLUGIN_URL + "/sslcode";

    public static final String TITAN_PLUGIN_WHITELIST_ADD_URL = TITAN_PLUGIN_URL + "/whitelist/add";
    public static final String TITAN_PLUGIN_WHITELIST_DELETE_URL = TITAN_PLUGIN_URL + "/whitelist/delete";
    public static final String TITAN_PLUGIN_LIST_ALL_DB_NAME_URL = TITAN_PLUGIN_URL + "/whitelist/listAllDbName";
    public static final String TITAN_PLUGIN_LIST_TITAN_KEY_URL = TITAN_PLUGIN_URL + "/whitelist/listTitanKey";

    public static final String TITAN_PLUGIN_FREE_VERIFY_ADD_URL = TITAN_PLUGIN_URL + "/freeverify/add";
    public static final String TITAN_PLUGIN_FREE_VERIFY_DELETE_URL = TITAN_PLUGIN_URL + "/freeverify/delete";
    public static final String TITAN_PLUGIN_PERMISSION_MERGE_URL = TITAN_PLUGIN_URL + "/config/permission/merge";

    public static final String TITAN_PLUGIN_DATA_WASH_URL = TITAN_PLUGIN_URL + "/config/datawash";

    public static final String TITAN_PLUGIN_BUILD_INDEX_URL = TITAN_PLUGIN_URL + "/index/build";

    public static final String TITAN_KEYS_APP_ID = "100010061";

    public PluginResponse addTitanKey(TitanKeyEntity titanKeyEntity, String env) {
        Transaction t = Cat.newTransaction("Titan.Plugin", "addTitanKey");
        PluginResponse pluginResponse = null;
        try {
            List<NameValuePair> urlParams = Lists.newArrayListWithCapacity(3);
            urlParams.add(new BasicNameValuePair("appid", TITAN_KEYS_APP_ID));
            urlParams.add(new BasicNameValuePair("titankey", titanKeyEntity.getKeyName()));
            urlParams.add(new BasicNameValuePair("env", env));
            String request = Utils.gson.toJson(titanKeyEntity);
            String response = HttpUtils.getInstance().sendPost(TITAN_PLUGIN_CONFIG_URL, urlParams, request);
            pluginResponse = Utils.gson.fromJson(response, PluginResponse.class);
            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            log.error("Add titan key[" + titanKeyEntity.getKeyName() + "] to plugin encounter error.", e);
            t.setStatus(e);
            Cat.logError(e);
        } finally {
            t.complete();
        }
        return pluginResponse;
    }

    public TitanKeyGetResponse getTitanKey(String titanKey, String env) {
        Transaction t = Cat.newTransaction("Titan.Plugin", "getTitanKey");
        TitanKeyGetResponse pluginResponse = null;
        try {
            List<NameValuePair> urlParams = Lists.newArrayListWithCapacity(3);
            urlParams.add(new BasicNameValuePair("appid", TITAN_KEYS_APP_ID));
            urlParams.add(new BasicNameValuePair("titankey", titanKey));
            urlParams.add(new BasicNameValuePair("env", env));
            String response = HttpUtils.getInstance().sendGet(TITAN_PLUGIN_CONFIG_URL, urlParams);
            pluginResponse = Utils.gson.fromJson(response, TitanKeyGetResponse.class);
            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            log.error("Get titan key[" + titanKey + "] from plugin encounter error.", e);
            t.setStatus(e);
            Cat.logError(e);
        } finally {
            t.complete();
        }
        return pluginResponse;
    }

    public PluginResponse updateTitanKey(TitanKeyUpdateRequest updateRequest, String operator) {
        Transaction t = Cat.newTransaction("Titan.Plugin", "updateTitanKey");
        PluginResponse pluginResponse = null;
        try {
            List<NameValuePair> urlParams = Lists.newArrayListWithCapacity(2);
            urlParams.add(new BasicNameValuePair("group", TITAN_KEYS_APP_ID));
            urlParams.add(new BasicNameValuePair("operator", operator));
            String request = Utils.gson.toJson(updateRequest);
            String response = HttpUtils.getInstance().sendPost(TITAN_PLUGIN_CONFIG_MHA_URL, urlParams, request);
            pluginResponse = Utils.gson.fromJson(response, PluginResponse.class);
            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            log.error("Update titan keys to plugin encounter error.", e);
            t.setStatus(e);
            Cat.logError(e);
        } finally {
            t.complete();
        }
        return pluginResponse;
    }

    public SslCodeGetResponse getSslCode(String env) {
        Transaction t = Cat.newTransaction("Titan.Plugin", "getSslCode");
        SslCodeGetResponse pluginResponse = null;
        try {
            List<NameValuePair> urlParams = Lists.newArrayListWithCapacity(2);
            urlParams.add(new BasicNameValuePair("appid", TITAN_KEYS_APP_ID));
            urlParams.add(new BasicNameValuePair("env", env));
            String response = HttpUtils.getInstance().sendGet(TITAN_PLUGIN_SSL_CODE_URL, urlParams);
            pluginResponse = Utils.gson.fromJson(response, SslCodeGetResponse.class);
            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            log.error("Get sslcode from plugin encounter error.", e);
            t.setStatus(e);
            Cat.logError(e);
        } finally {
            t.complete();
        }
        return pluginResponse;
    }

    public PluginResponse updateSslCode(String sslCode, String env, String operator) {
        Transaction t = Cat.newTransaction("Titan.Plugin", "updateSslCode");
        PluginResponse pluginResponse = null;
        try {
            List<NameValuePair> urlParams = Lists.newArrayListWithCapacity(3);
            urlParams.add(new BasicNameValuePair("appid", TITAN_KEYS_APP_ID));
            urlParams.add(new BasicNameValuePair("env", env));
            urlParams.add(new BasicNameValuePair("operator", operator));
            String request = sslCode;
            String response = HttpUtils.getInstance().sendPost(TITAN_PLUGIN_SSL_CODE_URL, urlParams, request);
            pluginResponse = Utils.gson.fromJson(response, PluginResponse.class);
            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            log.error("Update sslcode to plugin encounter error.", e);
            t.setStatus(e);
            Cat.logError(e);
        } finally {
            t.complete();
        }
        return pluginResponse;
    }

    public PluginResponse addPermissions(String titanKeys, String appIds, String runInBigData) {
        Transaction t = Cat.newTransaction("Titan.Plugin", "addPermissions");
        PluginResponse pluginResponse = null;
        try {
            Map<String, String> header = Maps.newLinkedHashMapWithExpectedSize(1);
            header.put("Content-Type", "application/x-www-form-urlencoded");
            List<NameValuePair> urlParams = Lists.newArrayListWithCapacity(3);
            urlParams.add(new BasicNameValuePair("titanKey", titanKeys));
            urlParams.add(new BasicNameValuePair("clientAppId", appIds));
            urlParams.add(new BasicNameValuePair("runInBigData", runInBigData));
            String response = HttpUtils.getInstance().sendPost(TITAN_PLUGIN_WHITELIST_ADD_URL, header, urlParams);
            pluginResponse = Utils.gson.fromJson(response, PluginResponse.class);
            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            log.error("Add titan key permissions to plugin encounter error.", e);
            t.setStatus(e);
            Cat.logError(e);
        } finally {
            t.complete();
        }
        return pluginResponse;
    }

    public PluginResponse deletePermissions(String titanKeys, String appIds) {
        Transaction t = Cat.newTransaction("Titan.Plugin", "deletePermissions");
        PluginResponse pluginResponse = null;
        try {
            Map<String, String> header = Maps.newLinkedHashMapWithExpectedSize(1);
            header.put("Content-Type", "application/x-www-form-urlencoded");
            List<NameValuePair> urlParams = Lists.newArrayListWithCapacity(2);
            urlParams.add(new BasicNameValuePair("titanKey", titanKeys));
            urlParams.add(new BasicNameValuePair("clientAppId", appIds));
            String response = HttpUtils.getInstance().sendPost(TITAN_PLUGIN_WHITELIST_DELETE_URL, header, urlParams);
            pluginResponse = Utils.gson.fromJson(response, PluginResponse.class);
            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            log.error("Delete titan key permissions from plugin encounter error.", e);
            t.setStatus(e);
            Cat.logError(e);
        } finally {
            t.complete();
        }
        return pluginResponse;
    }

    public PluginResponse addFreeVerify(FreeVerifyRequest freeVerifyRequest, String env) {
        Transaction t = Cat.newTransaction("Titan.Plugin", "addFreeVerify");
        PluginResponse pluginResponse = null;
        try {
            List<NameValuePair> urlParams = Lists.newArrayListWithCapacity(1);
            urlParams.add(new BasicNameValuePair("env", env));
            String request = Utils.gson.toJson(freeVerifyRequest);
            String response = HttpUtils.getInstance().sendPost(TITAN_PLUGIN_FREE_VERIFY_ADD_URL, urlParams, request);
            pluginResponse = Utils.gson.fromJson(response, PluginResponse.class);
            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            log.error("Add titan key[" + freeVerifyRequest.getTitanKeyList() + "] free verify to plugin encounter error.", e);
            t.setStatus(e);
            Cat.logError(e);
        } finally {
            t.complete();
        }
        return pluginResponse;
    }

    public PluginResponse deleteFreeVerify(FreeVerifyRequest freeVerifyRequest, String env) {
        Transaction t = Cat.newTransaction("Titan.Plugin", "deleteFreeVerify");
        PluginResponse pluginResponse = null;
        try {
            List<NameValuePair> urlParams = Lists.newArrayListWithCapacity(1);
            urlParams.add(new BasicNameValuePair("env", env));
            String request = Utils.gson.toJson(freeVerifyRequest);
            String response = HttpUtils.getInstance().sendPost(TITAN_PLUGIN_FREE_VERIFY_DELETE_URL, urlParams, request);
            pluginResponse = Utils.gson.fromJson(response, PluginResponse.class);
            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            log.error("Delete titan key[" + freeVerifyRequest.getTitanKeyList() + "] free verify to plugin encounter error.", e);
            t.setStatus(e);
            Cat.logError(e);
        } finally {
            t.complete();
        }
        return pluginResponse;
    }

    public PluginResponse washTitanKey(String env, String operator) {
        Transaction t = Cat.newTransaction("Titan.Plugin", "washTitanKey");
        PluginResponse pluginResponse = null;
        try {
            List<NameValuePair> urlParams = Lists.newArrayListWithCapacity(3);
            urlParams.add(new BasicNameValuePair("appid", TITAN_KEYS_APP_ID));
            urlParams.add(new BasicNameValuePair("env", env));
            urlParams.add(new BasicNameValuePair("operator", operator));
            String response = HttpUtils.getInstance().sendGet(TITAN_PLUGIN_DATA_WASH_URL, urlParams);
            pluginResponse = Utils.gson.fromJson(response, PluginResponse.class);
            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            log.error("Wash env[" + env + "] titan key from plugin encounter error.", e);
            t.setStatus(e);
            Cat.logError(e);
        } finally {
            t.complete();
        }
        return pluginResponse;
    }

    public TitanKeyListResponse listTitanKey(String env, String pageNo, String pageSize) {
        Transaction t = Cat.newTransaction("Titan.Plugin", "listTitanKey");
        TitanKeyListResponse pluginResponse = null;
        try {
            List<NameValuePair> urlParams = Lists.newArrayListWithCapacity(4);
            urlParams.add(new BasicNameValuePair("appid", TITAN_KEYS_APP_ID));
            urlParams.add(new BasicNameValuePair("env", env));
            urlParams.add(new BasicNameValuePair("pageNo", pageNo));
            urlParams.add(new BasicNameValuePair("pageSize", pageSize));
            String response = HttpUtils.getInstance().sendGet(TITAN_PLUGIN_CONFIGS_URL, urlParams);
            pluginResponse = Utils.gson.fromJson(response, TitanKeyListResponse.class);
            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            log.error("List titan key encounter error.", e);
            t.setStatus(e);
            Cat.logError(e);
        } finally {
            t.complete();
        }
        return pluginResponse;
    }

    public PluginResponse listTitanKeyByTime(String env, String beginTime) {
        Transaction t = Cat.newTransaction("Titan.Plugin", "listTitanKeyByTime");
        PluginResponse pluginResponse = null;
        try {
            List<NameValuePair> urlParams = Lists.newArrayListWithCapacity(3);
            urlParams.add(new BasicNameValuePair("appid", TITAN_KEYS_APP_ID));
            urlParams.add(new BasicNameValuePair("env", env));
            urlParams.add(new BasicNameValuePair("beginTime", beginTime));
            String response = HttpUtils.getInstance().sendGet(TITAN_PLUGIN_CONFIGS_BY_TIME_URL, urlParams);
            pluginResponse = Utils.gson.fromJson(response, PluginResponse.class);
            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            log.error("List titan key by time encounter error.", e);
            t.setStatus(e);
            Cat.logError(e);
        } finally {
            t.complete();
        }
        return pluginResponse;
    }

    public PluginResponse listAllDBName(String env) {
        Transaction t = Cat.newTransaction("Titan.Plugin", "listAllDBName");
        PluginResponse pluginResponse = null;
        try {
            List<NameValuePair> urlParams = Lists.newArrayListWithCapacity(1);
            urlParams.add(new BasicNameValuePair("env", env));
            String response = HttpUtils.getInstance().sendGet(TITAN_PLUGIN_LIST_ALL_DB_NAME_URL, urlParams);
            pluginResponse = Utils.gson.fromJson(response, PluginResponse.class);
            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            log.error("List all db name encounter error.", e);
            t.setStatus(e);
            Cat.logError(e);
        } finally {
            t.complete();
        }
        return pluginResponse;
    }

    public PluginResponse listTitanKeyByDBName(String env, String dbName) {
        Transaction t = Cat.newTransaction("Titan.Plugin", "listTitanKeyByDBName");
        PluginResponse pluginResponse = null;
        try {
            List<NameValuePair> urlParams = Lists.newArrayListWithCapacity(2);
            urlParams.add(new BasicNameValuePair("env", env));
            urlParams.add(new BasicNameValuePair("dbName", dbName));
            String response = HttpUtils.getInstance().sendGet(TITAN_PLUGIN_LIST_TITAN_KEY_URL, urlParams);
            pluginResponse = Utils.gson.fromJson(response, PluginResponse.class);
            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            log.error("List titan key by db name encounter error.", e);
            t.setStatus(e);
            Cat.logError(e);
        } finally {
            t.complete();
        }
        return pluginResponse;
    }

    public PluginResponse buildIndex(String env) {
        Transaction t = Cat.newTransaction("Titan.Plugin", "buildIndex");
        PluginResponse pluginResponse = null;
        try {
            List<NameValuePair> urlParams = Lists.newArrayListWithCapacity(1);
            urlParams.add(new BasicNameValuePair("env", env));
            String response = HttpUtils.getInstance().sendGet(TITAN_PLUGIN_BUILD_INDEX_URL, urlParams);
            pluginResponse = Utils.gson.fromJson(response, PluginResponse.class);
            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            log.error("Build index encounter error.", e);
            t.setStatus(e);
            Cat.logError(e);
        } finally {
            t.complete();
        }
        return pluginResponse;
    }

    public PluginResponse mergeTitanKeyPermission(TitanKeyEntity titanKeyEntity, String env) {
        Transaction t = Cat.newTransaction("Titan.Plugin", "mergeTitanKeyPermission");
        PluginResponse pluginResponse = null;
        try {
            List<NameValuePair> urlParams = Lists.newArrayListWithCapacity(2);
            urlParams.add(new BasicNameValuePair("titankey", titanKeyEntity.getKeyName()));
            urlParams.add(new BasicNameValuePair("env", env));
            String request = Utils.gson.toJson(titanKeyEntity);
            String response = HttpUtils.getInstance().sendPost(TITAN_PLUGIN_PERMISSION_MERGE_URL, urlParams, request);
            pluginResponse = Utils.gson.fromJson(response, PluginResponse.class);
            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            log.error("Merge white list to titan key[" + titanKeyEntity.getKeyName() + "] permission encounter error.", e);
            t.setStatus(e);
            Cat.logError(e);
        } finally {
            t.complete();
        }
        return pluginResponse;
    }

}
