package com.ctrip.framework.dal.dbconfig.plugin.service;

import com.ctrip.framework.dal.dbconfig.plugin.entity.AppIdIpCheckEntity;
import com.ctrip.framework.dal.dbconfig.plugin.entity.cms.*;
import com.ctrip.framework.dal.dbconfig.plugin.ignite.PluginIgniteConfig;
import com.ctrip.framework.dal.dbconfig.plugin.util.GsonUtils;
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
import static com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants.CMS_ACCESS_TOKEN;
import static com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants.HTTP_READ_TIMEOUT_MS;

/**
 * Created by lzyan on 2018/11/6.
 */
public class AppIdIpManager {
    private static Logger logger = LoggerFactory.getLogger(AppIdIpManager.class);
    private PluginIgniteConfig pluginIgniteConfig = PluginIgniteConfig.getInstance();
    private static final String CAT_TRANSACTION_FORMAT = "%s:%s";
    private static final String RETURN_FIELDS_ATTRIBUTE = "_returnFields";
    private static final String RESULT_BRANCH_ATTRIBUTE = "_resultBranch";
    private static final String RESULT_DEPTH_ATTRIBUTE = "_resultDepth";
    private static final String SERVER_IP_ATTRIBUTE = "server.ip";
    private static final String SERVER_ENTITY_STATUS_ATTRIBUTE = "server.entityStatus";
    private static final String APP_IN_ATTRIBUTE = "appId@in";
    private static final String APP_ID = "appId";
    private static final String GROUP = "group";
    private static final String GROUPS = "groups";
    private static final String ACCESS_GROUP_MEMBERS = "accessGroupMembers";
    private static final String WORKING_STATUS = "WORKING";

    public List<AppIdIpCheckEntity> getAllAppIdIp(String env) {
        List<AppIdIpCheckEntity> appIdIps = Lists.newLinkedList();

        // get all appIds
        List<String> appIds = getAllAppIds();

        // get batch count
        int batchCount = 100;   //default: 100
        String appIdIpCheckBatchCount = PluginIgniteConfig.getInstance().getIgniteParamValue(APPID_IP_CHECK_BATCH_FETCH_COUNT);
        if (!Strings.isNullOrEmpty(appIdIpCheckBatchCount)) {
            batchCount = Integer.parseInt(appIdIpCheckBatchCount);
        }

        // each time get 100 app ips
        List<List<String>> appGroup = Lists.partition(appIds, batchCount);  //每批100个
        List<AppIdIpCheckEntity> subAppIdIps = null;
        for (List<String> subAppGroup : appGroup) {
            subAppIdIps = getBatchAppIdIp(subAppGroup, env);
            if (subAppIdIps != null && !subAppIdIps.isEmpty()) {
                appIdIps.addAll(subAppIdIps);
            }
        }
        return appIdIps;
    }

    public List<String> getAllAppIds() {
        List<String> appIds = Lists.newLinkedList();
        Transaction t = Cat.newTransaction("AppIdIpCheck.GetAllAppIds", "getAllAppIds");
        try {
            String serviceUrl = pluginIgniteConfig.getIgniteParamValue(CMS_GET_APP_URL);
            String cmsAccessToken = pluginIgniteConfig.getIgniteParamValue(CMS_ACCESS_TOKEN);
            int timeoutMs = 60000;  // 60s
            String timeoutMsStr = pluginIgniteConfig.getIgniteParamValue(HTTP_READ_TIMEOUT_MS);
            if (!Strings.isNullOrEmpty(timeoutMsStr)) {
                timeoutMs = Integer.parseInt(timeoutMsStr);
            }
            Preconditions.checkArgument(!Strings.isNullOrEmpty(serviceUrl), "serviceUrl=null, please check config item 'cms.get.app.url'!");
            Preconditions.checkArgument(!Strings.isNullOrEmpty(cmsAccessToken), "cmsAccessToken=null, please check config item 'cms.access.token'!");

            // build request
            Map<String, Object> queries = Maps.newHashMap();
            queries.put(RETURN_FIELDS_ATTRIBUTE, Lists.newArrayList(APP_ID));
            CmsRequest cmsRequest = new CmsRequest(cmsAccessToken, queries);
            String requestBody = GsonUtils.t2Json(cmsRequest);

            t.addData("url", serviceUrl);
            t.addData("timeoutMs", timeoutMs);

            Map<String, String> headers = Maps.newHashMap();
            headers.put("Content-Type", "application/json");

            // get response
            String responseBody = RequestExecutor.getInstance().executePost(serviceUrl, headers, requestBody, timeoutMs);

            JsonParser parser = new JsonParser();
            JsonObject jsonObj = parser.parse(responseBody).getAsJsonObject();
            JsonArray jsonArray = jsonObj.get("data").getAsJsonArray();
            for (JsonElement appIdElement : jsonArray) {
                if (appIdElement != null && !appIdElement.isJsonNull()) {
                    JsonElement jsonAppId = ((JsonObject) appIdElement).get("appId");
                    String appId = jsonAppId.getAsString();
                    if (!Strings.isNullOrEmpty(appId)) {
                        appIds.add(appId);
                    }
                }
            }
            t.addData("appIds.size()", appIds.size());

            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            t.setStatus(e);
            Cat.logError("getAllAppIds(): get all appIds error!", e);
        } finally {
            t.complete();
        }
        return appIds;
    }

    public List<AppIdIpCheckEntity> getBatchAppIdIp(List<String> appIds, String env) {
        List<AppIdIpCheckEntity> appIdIps = Lists.newLinkedList();
        Transaction t = Cat.newTransaction("AppIdIpCheck.GetBatchAppIdIp", "getBatchAppIdIp");
        try {
            Preconditions.checkArgument((appIds != null && !appIds.isEmpty()), "appIds is null or empty, please check again!");
            Preconditions.checkArgument(!Strings.isNullOrEmpty(env), "env=null, please check again!");

            String serviceUrl = pluginIgniteConfig.getIgniteParamValue(CMS_GET_APP_URL);
            String cmsAccessToken = pluginIgniteConfig.getIgniteParamValue(CMS_ACCESS_TOKEN);
            int timeoutMs = 60000;  // 60s
            String timeoutMsStr = pluginIgniteConfig.getIgniteParamValue(HTTP_READ_TIMEOUT_MS);
            if (!Strings.isNullOrEmpty(timeoutMsStr)) {
                timeoutMs = Integer.parseInt(timeoutMsStr);
            }

            Preconditions.checkArgument(!Strings.isNullOrEmpty(serviceUrl), "serviceUrl=null, please check config item 'cms.get.app.url'!");
            Preconditions.checkArgument(!Strings.isNullOrEmpty(cmsAccessToken), "cmsAccessToken=null, please check config item 'cms.access.token'!");

            // build request
            Map<String, Object> queries = Maps.newHashMap();
            queries.put(APP_IN_ATTRIBUTE, appIds);
            queries.put(RESULT_BRANCH_ATTRIBUTE, GROUP);
            queries.put(RESULT_DEPTH_ATTRIBUTE, 1);
            queries.put(RETURN_FIELDS_ATTRIBUTE, Lists.newArrayList(APP_ID, GROUPS));
            CmsRequest cmsRequest = new CmsRequest(cmsAccessToken, queries);
            String requestBody = GsonUtils.t2Json(cmsRequest);

            t.addData("url", serviceUrl);
            t.addData("timeoutMs", timeoutMs);

            Map<String, String> headers = Maps.newHashMap();
            headers.put("Content-Type", "application/json");

            // get response
            String responseBody = RequestExecutor.getInstance().executePost(serviceUrl, headers, requestBody, timeoutMs);
            AppIpGetResponse response = GsonUtils.json2T(responseBody, AppIpGetResponse.class);

            if (response == null) {
                return appIdIps;
            }

            Boolean status = response.getStatus();
            List<App> apps = response.getData();
            if (status == null || apps == null || apps.isEmpty()) {
                return appIdIps;
            }

            if (status) {
                for (App app : apps) {
                    String appId = app.getAppId();
                    List<Group> groups = app.getGroups();
                    if (groups != null && !groups.isEmpty()) {
                        for (Group group : groups) {
                            List<GroupMember> groupMembers = group.getGroupMembers();
                            if (groupMembers != null && !groupMembers.isEmpty()) {
                                for (GroupMember groupMember : groupMembers) {
                                    String ip = groupMember.getIp();
                                    if (!Strings.isNullOrEmpty(ip)) {
                                        AppIdIpCheckEntity appIdIp = new AppIdIpCheckEntity();
                                        appIdIp.setClientAppId(appId);
                                        appIdIp.setClientIp(ip);
                                        appIdIp.setEnv(env);
                                        appIdIps.add(appIdIp);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            t.setStatus(e);
            Cat.logError("getBatchAppIdIp(): get batch appId-Ip error!", e);
        } finally {
            t.complete();
        }
        return appIdIps;
    }

    public Integer checkAppIdIp(AppIdIpCheckEntity appIdIp) {
        Integer returnCode = PAAS_RETURN_CODE_FAIL_INNER;
        Transaction t = Cat.newTransaction("AppIdIpCheck.CheckAppIdIp", String.format(CAT_TRANSACTION_FORMAT, appIdIp.getClientAppId(), appIdIp.getClientIp()));
        try {
            String serviceUrl = appIdIp.getServiceUrl();
            String cmsAccessToken = appIdIp.getServiceToken();
            String clientAppId = appIdIp.getClientAppId();
            String clientIp = appIdIp.getClientIp();
            int timeoutMs = appIdIp.getTimeoutMs();
            Preconditions.checkArgument(!Strings.isNullOrEmpty(serviceUrl), "appIdIpCheckServiceUrl=null, please check config item 'cms.get.app.url'!");
            Preconditions.checkArgument(!Strings.isNullOrEmpty(cmsAccessToken), "cmsAccessToken=null, please check again!");
            Preconditions.checkArgument(!Strings.isNullOrEmpty(clientAppId), "clientAppId=null, please check again!");
            Preconditions.checkArgument(!Strings.isNullOrEmpty(clientIp), "clientIp=null, please check again!");

            // build request
            Map<String, Object> queries = Maps.newHashMap();
            queries.put(SERVER_IP_ATTRIBUTE, clientIp);
            queries.put(SERVER_ENTITY_STATUS_ATTRIBUTE, WORKING_STATUS);
            queries.put(RETURN_FIELDS_ATTRIBUTE, Lists.newArrayList(APP_ID));
            CmsRequest cmsRequest = new CmsRequest(cmsAccessToken, queries);
            String requestBody = GsonUtils.t2Json(cmsRequest);

            t.addData("url", serviceUrl);
            t.addData("timeoutMs", timeoutMs);

            Map<String, String> headers = Maps.newHashMap();
            headers.put("Content-Type", "application/json");

            // get response
            String responseBody = RequestExecutor.getInstance().executePost(serviceUrl, headers, requestBody, timeoutMs);
            GroupGetResponse groupGetResponse = GsonUtils.json2T(responseBody, GroupGetResponse.class);

            if (groupGetResponse == null) {
                return PAAS_RETURN_CODE_FAIL_INNER;
            }

            Boolean status = groupGetResponse.getStatus();
            if (status == null || !status) {
                return PAAS_RETURN_CODE_FAIL_INNER;
            }

            if (status) {
                List<Group> groups = groupGetResponse.getData();
                if (groups == null || groups.isEmpty()) {
                    return PAAS_RETURN_CODE_NOT_MATCH;
                }

                for (Group group : groups) {
                    String appId = group.getAppId();
                    if (!Strings.isNullOrEmpty(appId) && appId.equalsIgnoreCase(clientAppId)) {
                        returnCode = PAAS_RETURN_CODE_SUCCESS;
                        break;
                    }
                }
            }

            if (returnCode != PAAS_RETURN_CODE_SUCCESS) {
                returnCode = PAAS_RETURN_CODE_NOT_MATCH;
            }

            t.setStatus(Message.SUCCESS);
        } catch (SocketTimeoutException e) {
            t.setStatus(Message.SUCCESS);
            Cat.logEvent("AppIdIpCheck.CheckAppIdIp.SocketTimeout",
                    String.format(CAT_TRANSACTION_FORMAT, appIdIp.getClientAppId(), appIdIp.getClientIp()), Event.SUCCESS, e.getMessage());
            logger.warn("checkAppIdIp(): check timeout, just let it go!", e);
        } catch (Exception e) {
            t.setStatus(e);
            Cat.logError("checkAppIdIp(): check error, just let it go!", e);
        } finally {
            t.complete();
        }
        return returnCode;
    }

}
