package com.ctrip.framework.db.cluster.service.plugin;

import com.ctrip.framework.db.cluster.domain.*;
import com.ctrip.framework.db.cluster.domain.plugin.titan.get.TitanKeyGetResponse;
import com.ctrip.framework.db.cluster.domain.plugin.titan.add.TitanKeyInfo;
import com.ctrip.framework.db.cluster.domain.plugin.titan.page.TitanKeyPageResponse;
import com.ctrip.framework.db.cluster.domain.plugin.titan.switches.TitanKeyMhaUpdateRequest;
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
import org.springframework.stereotype.Service;

import java.util.List;

import static com.ctrip.framework.db.cluster.util.Constants.TITAN_PLUGIN_APPID;

/**
 * Created by shenjie on 2019/3/18.
 */
@Slf4j
@Service
@AllArgsConstructor
public class TitanPluginService {

    private static final String GET = "/config";
    private static final String MHA_UPDATE = "/config/mha";
    private static final String PAGE_QUERY = "/configs";

    private final ConfigService configService;

    public PluginResponse addTitanKey(TitanKeyInfo titanKeyInfo, String env) {
        Transaction t = Cat.newTransaction("Titan.Plugin.Add.TitanKey", titanKeyInfo.getKeyName());
        try {
            List<NameValuePair> urlParams = Lists.newArrayListWithCapacity(3);
            urlParams.add(new BasicNameValuePair("appid", TITAN_PLUGIN_APPID));
            urlParams.add(new BasicNameValuePair("titankey", titanKeyInfo.getKeyName()));
            urlParams.add(new BasicNameValuePair("env", env));
            String request = Utils.gson.toJson(titanKeyInfo);
            String url = configService.getPluginTitanUrl();
            String response = HttpUtils.getInstance().sendPost(url, urlParams, request, configService.getHttpReadTimeoutInMs());
            PluginResponse pluginResponse = Utils.gson.fromJson(response, PluginResponse.class);
            t.setStatus(Message.SUCCESS);

            return pluginResponse;
        } catch (Exception e) {
            t.setStatus(e);
            Cat.logError(e);
            throw new DBClusterServiceException(e);
        } finally {
            t.complete();
        }
    }

    public PluginResponse mhaUpdate(TitanKeyMhaUpdateRequest titanKeyMhaUpdateRequest, String operator) {
        Transaction t = Cat.newTransaction("Titan.Plugin.MhaUpdate", "mhaUpdate");
        try {
            List<NameValuePair> urlParams = Lists.newArrayListWithCapacity(2);
            urlParams.add(new BasicNameValuePair("group", TITAN_PLUGIN_APPID));
            urlParams.add(new BasicNameValuePair("operator", operator));
            String request = Utils.gson.toJson(titanKeyMhaUpdateRequest);
            t.addData("request", request);
            String url = configService.getPluginTitanUrl() + MHA_UPDATE;
            String response = HttpUtils.getInstance().sendPost(url, urlParams, request, configService.getHttpReadTimeoutInMs());
            PluginResponse pluginResponse = Utils.gson.fromJson(response, PluginResponse.class);
            t.setStatus(Message.SUCCESS);

            return pluginResponse;
        } catch (Exception e) {
            t.setStatus(e);
            Cat.logError(e);
            throw new DBClusterServiceException(e);
        } finally {
            t.complete();
        }
    }

    public TitanKeyGetResponse getTitanKey(String titanKey, String env) {
        Transaction t = Cat.newTransaction("Titan.Plugin.Get.TitanKey", titanKey);
        try {
            List<NameValuePair> urlParams = Lists.newArrayListWithCapacity(3);
            urlParams.add(new BasicNameValuePair("appid", TITAN_PLUGIN_APPID));
            urlParams.add(new BasicNameValuePair("titankey", titanKey));
            urlParams.add(new BasicNameValuePair("env", env));
            final String url = configService.getPluginTitanUrl() + GET;
            String response = HttpUtils.getInstance().sendGet(url, urlParams, configService.getHttpReadTimeoutInMs());
            TitanKeyGetResponse pluginResponse = Utils.gson.fromJson(response, TitanKeyGetResponse.class);
            t.setStatus(Message.SUCCESS);

            return pluginResponse;
        } catch (Exception e) {
            log.error("Get titan key[" + titanKey + "] from plugin encounter error.", e);
            t.setStatus(e);
            Cat.logError(e);
            throw new DBClusterServiceException(e);
        } finally {
            t.complete();
        }
    }

    public TitanKeyPageResponse pageQueryTitanKeys(final Integer pageNo, final Integer pageSize, final String env) {
        Transaction t = Cat.newTransaction("Titan.Plugin.pageQuery", "pageQuery");
        try {
            List<NameValuePair> urlParams = Lists.newArrayListWithCapacity(4);
            urlParams.add(new BasicNameValuePair("appid", TITAN_PLUGIN_APPID));
            urlParams.add(new BasicNameValuePair("pageNo", pageNo.toString()));
            urlParams.add(new BasicNameValuePair("pageSize", pageSize.toString()));
            urlParams.add(new BasicNameValuePair("env", env));
            final String url = configService.getPluginTitanUrl() + PAGE_QUERY;
            String response = HttpUtils.getInstance().sendGet(url, urlParams, configService.getHttpReadTimeoutInMs());
            TitanKeyPageResponse pluginResponse = Utils.gson.fromJson(response, TitanKeyPageResponse.class);
            t.setStatus(Message.SUCCESS);

            return pluginResponse;
        } catch (Exception e) {
            t.setStatus(e);
            Cat.logError(e);
            throw new DBClusterServiceException(e);
        } finally {
            t.complete();
        }
    }

    // deprecated
//    public PluginResponse retry(TitanOperationType operationType, String requestBody, Map<String, String> params) {
//        Transaction t = Cat.newTransaction("Titan.Plugin.Retry", operationType.name());
//        PluginResponse pluginResponse;
//        try {
//            List<NameValuePair> urlParams = Lists.newArrayList();
//            if (params != null && !params.isEmpty()) {
//                for (Map.Entry<String, String> param : params.entrySet()) {
//                    urlParams.add(new BasicNameValuePair(param.getKey(), param.getValue()));
//                }
//            }
//
//            String url = getUrl(operationType);
//            t.addData("url", url);
//            t.addData("params", params);
//            t.addData("request", requestBody);
//
//            String response = HttpUtils.getInstance().sendPost(url, urlParams, requestBody, configService.getHttpReadTimeoutInMs());
//            pluginResponse = Utils.gson.fromJson(response, PluginResponse.class);
//            t.setStatus(Message.SUCCESS);
//        } catch (Exception e) {
//            t.setStatus(e);
//            throw new DBClusterServiceException(e);
//        } finally {
//            t.complete();
//        }
//        return pluginResponse;
//    }
}
