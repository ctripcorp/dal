package com.ctrip.framework.dal.dbconfig.plugin.service;

import com.ctrip.framework.dal.dbconfig.plugin.entity.AppIdIpCheckEntity;
import com.ctrip.framework.dal.dbconfig.plugin.ignite.PluginIgniteConfig;
import com.ctrip.framework.dal.dbconfig.plugin.util.CommonHelper;
import com.ctrip.framework.dal.dbconfig.plugin.util.RequestExecutor;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Map;

import static com.ctrip.framework.dal.dbconfig.plugin.constant.CommonConstants.*;
import static com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants.APPID_IP_CHECK_SERVICE_TOKEN;
import static com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants.HTTP_READ_TIMEOUT_MS;

/**
 * Created by lzyan on 2018/11/6.
 */
public class AppIdIpManager {
    private static Logger logger = LoggerFactory.getLogger(AppIdIpManager.class);
    private PluginIgniteConfig pluginIgniteConfig = PluginIgniteConfig.getInstance();

    // fetch all exist appId-Ip relationship
    public List<AppIdIpCheckEntity> fetchAllExistAppIdIp(String env) {
        List<AppIdIpCheckEntity> appIdIpCheckEntityList = Lists.newLinkedList();

        // get batch count config
        int batchCount = 100;   //Default: 100
        String appIdIpCheckBatchFetchCount = PluginIgniteConfig.getInstance().getIgniteParamValue(APPID_IP_CHECK_BATCH_FETCH_COUNT);
        if (!Strings.isNullOrEmpty(appIdIpCheckBatchFetchCount)) {
            batchCount = Integer.parseInt(appIdIpCheckBatchFetchCount);
        }

        List<String> appIdList = fetchAllAppId();
        List<List<String>> subGroupList = Lists.partition(appIdList, batchCount);  //每批100个
        List<AppIdIpCheckEntity> tmpList = null;
        for (List<String> subList : subGroupList) {
            tmpList = batchFetchAppIdIp(subList, env);
            if (tmpList != null && !tmpList.isEmpty()) {
                appIdIpCheckEntityList.addAll(tmpList);
            }
        }
        return appIdIpCheckEntityList;
    }


    // fetch all appId
    public List<String> fetchAllAppId() {
        List<String> appIdList = Lists.newLinkedList();
        Transaction t = Cat.newTransaction("AppIdIpCheckServiceValidator", "fetchAllAppId");
        try {
            // Sample: http://paas.ctripcorp.com/api/v2/titan/all_app_ids/
            String serviceUrl = pluginIgniteConfig.getIgniteParamValue(APPID_IP_CHECK_FETCH_ALL_APPID_URL);
            int timeoutMs = 60000;  // 60s
            String timeoutMsStr = pluginIgniteConfig.getIgniteParamValue(HTTP_READ_TIMEOUT_MS);
            if (!Strings.isNullOrEmpty(timeoutMsStr)) {
                timeoutMs = Integer.parseInt(timeoutMsStr);
            }
            Preconditions.checkArgument(!Strings.isNullOrEmpty(serviceUrl), "serviceUrl=null, please check config item 'appId.ip.check.fetch.all.appId.url'!");

            t.addData("url", serviceUrl);
            t.addData("timeoutMs", timeoutMs);

            Map<String, String> headers = Maps.newHashMap();
            headers.put("Content-Type", "application/json");
            String body = RequestExecutor.getInstance().executeGet(serviceUrl, headers, timeoutMs);
            /**
             *  Sample:
             {
             "data": [10964,100017786,100017787],
             "return_code": 0,
             "error_msg": ""
             }
             */
            JsonParser parser = new JsonParser();
            JsonObject jsonObj = parser.parse(body).getAsJsonObject();
            JsonArray jsonArray = jsonObj.get("data").getAsJsonArray();
            for (JsonElement appId : jsonArray) {
                if (appId != null && !appId.isJsonNull()) {
                    appIdList.add(appId.getAsString());
                }
            }
            t.addData("appIdList.size()", appIdList.size());

            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            t.setStatus(e);
            Cat.logError("fetchAllAppId(): fetch all appId error!", e);
        } finally {
            t.complete();
        }
        return appIdList;
    }


    // batch fetch appId-Ip relationship
    public List<AppIdIpCheckEntity> batchFetchAppIdIp(List<String> appIdList, String env) {
        List<AppIdIpCheckEntity> appIdIpCheckEntityList = Lists.newLinkedList();
        Transaction t = Cat.newTransaction("AppIdIpCheckServiceValidator", "batchFetchAppIdIp");
        try {
            Preconditions.checkArgument((appIdList != null && !appIdList.isEmpty()), "appIdList is null or empty, please check again!");
            Preconditions.checkArgument(!Strings.isNullOrEmpty(env), "env=null, please check again!");

            // Sample: http://paas.ctripcorp.com/api/v2/titan/cache_ips/
            String serviceUrl = pluginIgniteConfig.getIgniteParamValue(APPID_IP_CHECK_BATCH_FETCH_RELATION_URL);
            int timeoutMs = 60000;  // 60s
            String timeoutMsStr = pluginIgniteConfig.getIgniteParamValue(HTTP_READ_TIMEOUT_MS);
            if (!Strings.isNullOrEmpty(timeoutMsStr)) {
                timeoutMs = Integer.parseInt(timeoutMsStr);
            }
            String serviceToken = pluginIgniteConfig.getIgniteParamValue(APPID_IP_CHECK_SERVICE_TOKEN);
            Preconditions.checkArgument(!Strings.isNullOrEmpty(serviceUrl), "serviceUrl=null, please check config item 'appId.ip.check.batch.fetch.relation.url'!");

            // {"app_ids":["100000716","100006124","100002312","100001680"], "env":"pro"}
            String request = buildBatchFetchRequest(appIdList, env);

            t.addData("url", serviceUrl);
            t.addData("request", request);
            t.addData("timeoutMs", timeoutMs);

            Map<String, String> headers = Maps.newHashMap();
            headers.put("Content-Type", "application/json");
            if (!Strings.isNullOrEmpty(serviceToken)) {
                headers.put("X-Service-Token", serviceToken);
            }
            String body = RequestExecutor.getInstance().executePost(serviceUrl, headers, request, timeoutMs);
            /**
             * Sample:
             * {"data": [{"ips": ["10.2.130.219","10.2.132.43"],"app_id": "100002312"},{"ips": null,"app_id": "100001680"}], "return_code":0, "error_msg":""}
             */
            appIdIpCheckEntityList = parseBatchFetchBody(body, env);

            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            t.setStatus(e);
            Cat.logError("batchFetchAppIdIp(): batch fetch appId-Ip error!", e);
        } finally {
            t.complete();
        }
        return appIdIpCheckEntityList;
    }

    /**
     * check appId-IP
     * [1] 如果请求中有异常, 默认放过(将 returnCode=4 放入临时缓存)
     *
     * @return
     */
    public Integer checkAppIdIp(AppIdIpCheckEntity appIdIpCheckEntity) {
        Integer returnCode = PAAS_RETURN_CODE_FAIL_INNER;
        Transaction t = Cat.newTransaction("AppIdIpCheckServiceValidator", "checkAppIdIp");
        try {
            // Sample: http://paas.ctripcorp.com/api/v2/titan/verify/
            String appIdIpCheckServiceUrl = appIdIpCheckEntity.getServiceUrl();
            String clientAppId = appIdIpCheckEntity.getClientAppId();
            String clientIp = appIdIpCheckEntity.getClientIp();
            String env = appIdIpCheckEntity.getEnv();
            String serviceToken = appIdIpCheckEntity.getServiceToken();
            int timeoutMs = appIdIpCheckEntity.getTimeoutMs();
            Preconditions.checkArgument(!Strings.isNullOrEmpty(appIdIpCheckServiceUrl), "appIdIpCheckServiceUrl=null, please check config item 'appId.ip.check.service.url'!");
            Preconditions.checkArgument(!Strings.isNullOrEmpty(clientAppId), "clientAppId=null, please check again!");
            Preconditions.checkArgument(!Strings.isNullOrEmpty(clientIp), "clientIp=null, please check again!");
            Preconditions.checkArgument(!Strings.isNullOrEmpty(env), "env=null, please check again!");

            // {"app_id": "100006124", "env": "fat", "ip": "10.5.55.161"}
            String request = String.format(APPID_IP_CHECK_REQUEST_TEMPLATE, clientAppId, env, clientIp);

            t.addData("url", appIdIpCheckServiceUrl);
            t.addData("request", request);
            t.addData("timeoutMs", timeoutMs);

            Map<String, String> headers = Maps.newHashMap();
            headers.put("Content-Type", "application/json");
            if (!Strings.isNullOrEmpty(serviceToken)) {
                headers.put("X-Service-Token", serviceToken);
            }
            String body = RequestExecutor.getInstance().executePost(appIdIpCheckServiceUrl, headers, request, timeoutMs);
            //Sample:   {"return_code": 0, "error_msg": ""}
            JsonParser parser = new JsonParser();
            JsonObject jsonObj = parser.parse(body).getAsJsonObject();
            returnCode = jsonObj.get("return_code").getAsInt();
            Cat.logEvent("AppIdIpCheckService.Validator.ReturnCode", String.valueOf(returnCode), Event.SUCCESS, "body=" + body);


            t.setStatus(Message.SUCCESS);
        } catch (SocketTimeoutException e) {
            t.setStatus(Message.SUCCESS);
            Cat.logEvent("AppIdIpCheckServiceValidator", "APPID_IP_CHECK_TIMEOUT", Event.SUCCESS, e.getMessage());
            logger.warn("checkAppIdIp(): check timeout, just let it go!", e);
        } catch (Exception e) {
            t.setStatus(e);
            Cat.logError("checkAppIdIp(): check error, just let it go!", e);
        } finally {
            t.complete();
        }
        return returnCode;
    }


    // build batch fetch request
    private String buildBatchFetchRequest(List<String> appIdList, String env) {
        boolean isFirst = true;
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"app_ids\":[");
        for (String appId : appIdList) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(",");
            }
            sb.append("\"").append(appId).append("\"");
        }
        sb.append("], ");
        sb.append("\"env\":\"").append(env).append("\"");
        sb.append("}");
        String request = sb.toString();
        return request;
    }


    /**
     * parse batch fetch body
     * Sample:
     * {
     * "data": [
     * {
     * "ips": [
     * "10.2.130.219",
     * "10.2.132.43"
     * ],
     * "app_id": "100002312"
     * },
     * {
     * "ips": null,
     * "app_id": "100001680"
     * }
     * ],
     * "return_code": 0,
     * "error_msg": ""
     * }
     */
    private List<AppIdIpCheckEntity> parseBatchFetchBody(String body, String env) {
        List<AppIdIpCheckEntity> appIdIpCheckEntityList = Lists.newLinkedList();
        AppIdIpCheckEntity appIdIpCheckEntity = null;
        JsonParser parser = new JsonParser();
        JsonObject jsonObj = parser.parse(body).getAsJsonObject();
        JsonArray dataArray = jsonObj.get("data").getAsJsonArray();
        if (dataArray != null && !dataArray.isJsonNull()) {
            JsonObject subData = null;
            String appId = null;
            JsonArray ipArray = null;
            JsonElement je4Ips = null;
            for (int i = 0; i < dataArray.size(); i++) {
                subData = dataArray.get(i).getAsJsonObject();
                if (subData != null && !subData.isJsonNull()) {
                    appId = subData.get("app_id").getAsString();
                    ipArray = null;
                    je4Ips = subData.get("ips");
                    if (je4Ips != null && !je4Ips.isJsonNull()) {
                        ipArray = je4Ips.getAsJsonArray();
                    }
                    if (!Strings.isNullOrEmpty(appId) && ipArray != null && !ipArray.isJsonNull()) {
                        String ip = null;
                        for (int j = 0; j < ipArray.size(); j++) {
                            ip = CommonHelper.getStringValue(ipArray.get(j));
                            if (!Strings.isNullOrEmpty(ip)) {
                                appIdIpCheckEntity = new AppIdIpCheckEntity();
                                appIdIpCheckEntity.setClientAppId(appId);
                                appIdIpCheckEntity.setClientIp(ip);
                                appIdIpCheckEntity.setEnv(env);
                                appIdIpCheckEntityList.add(appIdIpCheckEntity);
                            }

                        }
                    }
                }
            }//End for<dataArray>
        }
        return appIdIpCheckEntityList;
    }


}
