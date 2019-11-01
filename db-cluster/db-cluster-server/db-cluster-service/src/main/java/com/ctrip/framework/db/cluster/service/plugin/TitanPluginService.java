package com.ctrip.framework.db.cluster.service.plugin;

import com.ctrip.framework.db.cluster.domain.*;
import com.ctrip.framework.db.cluster.domain.plugin.titan.TitanKeyGetResponse;
import com.ctrip.framework.db.cluster.domain.plugin.titan.TitanKeyInfo;
import com.ctrip.framework.db.cluster.domain.plugin.titan.switches.TitanMhaUpdateRequest;
import com.ctrip.framework.db.cluster.domain.plugin.titan.TitanUpdateRequest;
import com.ctrip.framework.db.cluster.enums.TitanOperationType;
import com.ctrip.framework.db.cluster.exception.DBClusterServiceException;
import com.ctrip.framework.db.cluster.service.config.ConfigService;
import com.ctrip.framework.db.cluster.util.HttpUtils;
import com.ctrip.framework.db.cluster.util.Utils;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.ctrip.framework.db.cluster.util.Constants.TITAN_PLUGIN_APPID;

/**
 * Created by shenjie on 2019/3/18.
 */
@Slf4j
@Service
@AllArgsConstructor
public class TitanPluginService {

    private static final String SWITCH = "/update";
    private static final String MHA_UPDATE = "/mha";

    private final ConfigService configService;

    public PluginResponse addTitanKey(TitanKeyInfo titanKeyInfo, String env) {
        Transaction t = Cat.newTransaction("Titan.Plugin.Add.TitanKey", titanKeyInfo.getKeyName());
        PluginResponse pluginResponse;
        try {
            List<NameValuePair> urlParams = Lists.newArrayListWithCapacity(3);
            urlParams.add(new BasicNameValuePair("appid", TITAN_PLUGIN_APPID));
            urlParams.add(new BasicNameValuePair("titankey", titanKeyInfo.getKeyName()));
            urlParams.add(new BasicNameValuePair("env", env));
            String request = Utils.gson.toJson(titanKeyInfo);
            String url = configService.getPluginTitanUrl();
            String response = HttpUtils.getInstance().sendPost(url, urlParams, request, configService.getHttpReadTimeoutInMs());
            pluginResponse = Utils.gson.fromJson(response, PluginResponse.class);
            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            t.setStatus(e);
            Cat.logError(e);
            throw new DBClusterServiceException(e);
        } finally {
            t.complete();
        }
        return pluginResponse;
    }

    public PluginResponse switchTitanKey(TitanUpdateRequest titanUpdateRequest, String operator) {
        Transaction t = Cat.newTransaction("Titan.Plugin.Update.TitanKey", "UpdateTitanKey");
        PluginResponse pluginResponse;
        try {
            List<NameValuePair> urlParams = Lists.newArrayListWithCapacity(1);
            urlParams.add(new BasicNameValuePair("operator", operator));
            String request = Utils.gson.toJson(titanUpdateRequest);
            t.addData("request", request);
            String url = configService.getPluginTitanUrl() + SWITCH;
            String response = HttpUtils.getInstance().sendPost(url, urlParams, request, configService.getHttpReadTimeoutInMs());
            pluginResponse = Utils.gson.fromJson(response, PluginResponse.class);
            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            t.setStatus(e);
            throw new DBClusterServiceException(e);
        } finally {
            t.complete();
        }
        return pluginResponse;
    }

    public PluginResponse retry(TitanOperationType operationType, String requestBody, Map<String, String> params) {
        Transaction t = Cat.newTransaction("Titan.Plugin.Retry", operationType.name());
        PluginResponse pluginResponse;
        try {
            List<NameValuePair> urlParams = Lists.newArrayList();
            if (params != null && !params.isEmpty()) {
                for (Map.Entry<String, String> param : params.entrySet()) {
                    urlParams.add(new BasicNameValuePair(param.getKey(), param.getValue()));
                }
            }

            String url = getUrl(operationType);
            t.addData("url", url);
            t.addData("params", params);
            t.addData("request", requestBody);

            String response = HttpUtils.getInstance().sendPost(url, urlParams, requestBody, configService.getHttpReadTimeoutInMs());
            pluginResponse = Utils.gson.fromJson(response, PluginResponse.class);
            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            t.setStatus(e);
            throw new DBClusterServiceException(e);
        } finally {
            t.complete();
        }
        return pluginResponse;
    }

    public PluginResponse mhaUpdate(TitanMhaUpdateRequest titanMhaUpdateRequest, String operator) {
        Transaction t = Cat.newTransaction("Titan.Plugin.MhaUpdate", "mhaUpdate");
        PluginResponse pluginResponse;
        try {
            List<NameValuePair> urlParams = Lists.newArrayListWithCapacity(2);
            urlParams.add(new BasicNameValuePair("group", TITAN_PLUGIN_APPID));
            urlParams.add(new BasicNameValuePair("operator", operator));
            String request = Utils.gson.toJson(titanMhaUpdateRequest);
            t.addData("request", request);
            String url = configService.getPluginTitanUrl() + MHA_UPDATE;
            String response = HttpUtils.getInstance().sendPost(url, urlParams, request, configService.getHttpReadTimeoutInMs());
            pluginResponse = Utils.gson.fromJson(response, PluginResponse.class);
            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            t.setStatus(e);
            Cat.logError(e);
            throw new DBClusterServiceException(e);
        } finally {
            t.complete();
        }
        return pluginResponse;
    }

    public TitanKeyGetResponse getTitanKey(String titanKey, String env) {
        Transaction t = Cat.newTransaction("Titan.Plugin.Get.TitanKey", titanKey);
        TitanKeyGetResponse pluginResponse = null;
        try {
            List<NameValuePair> urlParams = Lists.newArrayListWithCapacity(3);
            urlParams.add(new BasicNameValuePair("appid", TITAN_PLUGIN_APPID));
            urlParams.add(new BasicNameValuePair("titankey", titanKey));
            urlParams.add(new BasicNameValuePair("env", env));
            String response = HttpUtils.getInstance().sendGet(configService.getPluginTitanUrl(), urlParams, configService.getHttpReadTimeoutInMs());
            pluginResponse = Utils.gson.fromJson(response, TitanKeyGetResponse.class);
            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            log.error("Get titan key[" + titanKey + "] from plugin encounter error.", e);
            t.setStatus(e);
            Cat.logError(e);
            throw new DBClusterServiceException(e);
        } finally {
            t.complete();
        }
        return pluginResponse;
    }

    private String getUrl(TitanOperationType operation) {
        String baseUrl = configService.getPluginTitanUrl();
        switch (operation) {
            case ADD:
                return baseUrl;
            case UPDATE:
                return baseUrl;
            case SWITCH:
                return baseUrl + SWITCH;
            default:
                return "";
        }
    }

}
