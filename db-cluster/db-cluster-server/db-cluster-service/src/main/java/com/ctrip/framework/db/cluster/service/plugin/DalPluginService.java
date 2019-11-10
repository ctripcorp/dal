package com.ctrip.framework.db.cluster.service.plugin;

import com.ctrip.framework.db.cluster.domain.*;
import com.ctrip.framework.db.cluster.domain.plugin.dal.DalClusterGetResponse;
import com.ctrip.framework.db.cluster.domain.plugin.dal.DalClusterUpdateRequest;
import com.ctrip.framework.db.cluster.domain.plugin.dal.ReleaseCluster;
import com.ctrip.framework.db.cluster.exception.DBClusterServiceException;
import com.ctrip.framework.db.cluster.service.config.ConfigService;
import com.ctrip.framework.db.cluster.util.HttpUtils;
import com.ctrip.framework.db.cluster.util.Utils;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by shenjie on 2019/5/8.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DalPluginService {

    private static final String release = "/release";

    private final ConfigService configService;


    // below are deprecated
    private static final String UPDATE = "/update";
    private static final String GET = "/info";

    public PluginResponse releaseClusters(List<ReleaseCluster> clusters, String env, String operator) {
        Transaction t = Cat.newTransaction("Dal.Plugin.AddCluster", "addClusters");
        PluginResponse pluginResponse;
        try {
            List<NameValuePair> urlParams = Lists.newArrayList();
            urlParams.add(new BasicNameValuePair("env", env));
            urlParams.add(new BasicNameValuePair("operator", operator));
            String url = configService.getPluginDalUrl() + release;
            String requestBody = Utils.gson.toJson(clusters);
            String responseBody = HttpUtils.getInstance().sendPost(url, urlParams, requestBody, configService.getHttpReadTimeoutInMs());
            pluginResponse = Utils.gson.fromJson(responseBody, PluginResponse.class);
            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            t.setStatus(e);
            throw new DBClusterServiceException(e);
        } finally {
            t.complete();
        }
        return pluginResponse;
    }


    // below are deprecated
    public PluginResponse updateClusters(List<ReleaseCluster> clusters, String env, String operator) {
        DalClusterUpdateRequest updateRequest = DalClusterUpdateRequest.builder()
                .env(env)
                .data(clusters)
                .build();
        return update(updateRequest, operator);
    }

    private PluginResponse update(DalClusterUpdateRequest updateRequest, String operator) {
        Transaction t = Cat.newTransaction("Dal.Plugin.UpdateCluster", "updateClusters");
        PluginResponse pluginResponse;
        try {
            List<NameValuePair> urlParams = Lists.newArrayList();
            urlParams.add(new BasicNameValuePair("operator", operator));
            String url = configService.getPluginDalUrl() + UPDATE;
            String requestBody = Utils.gson.toJson(updateRequest);
            String responseBody = HttpUtils.getInstance().sendPost(url, urlParams, requestBody, configService.getHttpReadTimeoutInMs());
            pluginResponse = Utils.gson.fromJson(responseBody, PluginResponse.class);
            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            t.setStatus(e);
            throw new DBClusterServiceException(e);
        } finally {
            t.complete();
        }
        return pluginResponse;
    }

    public DalClusterGetResponse get(String clusterName, String env) {
        Transaction t = Cat.newTransaction("Dal.Plugin.GetCluster", clusterName);
        DalClusterGetResponse pluginResponse = null;
        try {
            List<NameValuePair> urlParams = Lists.newArrayList();
            urlParams.add(new BasicNameValuePair("clustername", clusterName));
            urlParams.add(new BasicNameValuePair("env", env));
            String url = configService.getPluginDalUrl() + GET;
            String response = HttpUtils.getInstance().sendGet(url, urlParams, configService.getHttpReadTimeoutInMs());
            pluginResponse = Utils.gson.fromJson(response, DalClusterGetResponse.class);
            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            log.error("Get dal cluster[" + clusterName + "] from plugin encounter error.", e);
            t.setStatus(e);
            Cat.logError(e);
            throw new DBClusterServiceException(e);
        } finally {
            t.complete();
        }
        return pluginResponse;
    }
}
