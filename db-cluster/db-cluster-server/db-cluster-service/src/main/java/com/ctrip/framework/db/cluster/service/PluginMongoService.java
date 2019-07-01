package com.ctrip.framework.db.cluster.service;

import com.ctrip.framework.db.cluster.config.ConfigService;
import com.ctrip.framework.db.cluster.domain.MongoCluster;
import com.ctrip.framework.db.cluster.domain.MongoClusterGetResponse;
import com.ctrip.framework.db.cluster.domain.PluginResponse;
import com.ctrip.framework.db.cluster.exception.DBClusterServiceException;
import com.ctrip.framework.db.cluster.util.HttpUtil;
import com.ctrip.framework.db.cluster.util.Util;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by shenjie on 2019/4/2.
 */
@Slf4j
@Service
public class PluginMongoService {

    private static final String ADD_OPERATOR = "/add";
    private static final String GET_OPERATOR = "/info";
    private static final String UPDATE_OPERATOR = "/update";

    @Autowired
    private ConfigService configService;

    public PluginResponse add(MongoCluster mongoCluster, String env, String subEnv, String operator) throws DBClusterServiceException {
        Transaction t = Cat.newTransaction("Plugin.Add.Mongo.Cluster", mongoCluster.getClusterName());
        PluginResponse pluginResponse;
        try {
            String url = configService.getPluginMongoUrl() + ADD_OPERATOR;
            pluginResponse = getResponse(mongoCluster, env, subEnv, operator, url);
            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            log.error("Add mongo cluster[" + mongoCluster.getClusterName() + "] to plugin encounter error.", e);
            t.setStatus(e);
            Cat.logError(e);
            throw new DBClusterServiceException(e);
        } finally {
            t.complete();
        }
        return pluginResponse;
    }

    public PluginResponse update(MongoCluster mongoCluster, String env, String subEnv, String operator) throws DBClusterServiceException {
        Transaction t = Cat.newTransaction("Plugin.Update.Mongo.Cluster", mongoCluster.getClusterName());
        PluginResponse pluginResponse;
        try {
            String url = configService.getPluginMongoUrl() + UPDATE_OPERATOR;
            pluginResponse = getResponse(mongoCluster, env, subEnv, operator, url);
            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            log.error("Update mongo cluster[" + mongoCluster.getClusterName() + "] to plugin encounter error.", e);
            t.setStatus(e);
            Cat.logError(e);
            throw new DBClusterServiceException(e);
        } finally {
            t.complete();
        }
        return pluginResponse;
    }

    public MongoClusterGetResponse get(String clusterName, String env, String subEnv) throws DBClusterServiceException {
        Transaction t = Cat.newTransaction("Plugin.Get.Mongo.Cluster", clusterName);
        MongoClusterGetResponse pluginResponse;
        try {
            List<NameValuePair> urlParams = Lists.newArrayListWithCapacity(3);
            urlParams.add(new BasicNameValuePair("clustername", clusterName));
            urlParams.add(new BasicNameValuePair("env", env));
            urlParams.add(new BasicNameValuePair("subenv", subEnv));
            String url = configService.getPluginMongoUrl() + GET_OPERATOR;
            String response = HttpUtil.getInstance().sendGet(url, urlParams);
            pluginResponse = Util.gson.fromJson(response, MongoClusterGetResponse.class);
            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            log.error("Get mongo cluster[" + clusterName + "] from plugin encounter error.", e);
            t.setStatus(e);
            Cat.logError(e);
            throw new DBClusterServiceException(e);
        } finally {
            t.complete();
        }
        return pluginResponse;
    }

    private PluginResponse getResponse(MongoCluster mongoCluster, String env, String subEnv, String operator, String url) throws Exception {
        List<NameValuePair> urlParams = Lists.newArrayListWithCapacity(3);
        urlParams.add(new BasicNameValuePair("env", env));
        urlParams.add(new BasicNameValuePair("subenv", subEnv));
        urlParams.add(new BasicNameValuePair("operator", operator));
        String request = Util.gson.toJson(mongoCluster);
        String response = HttpUtil.getInstance().sendPost(url, urlParams, request);
        PluginResponse pluginResponse = Util.gson.fromJson(response, PluginResponse.class);
        return pluginResponse;
    }
}
