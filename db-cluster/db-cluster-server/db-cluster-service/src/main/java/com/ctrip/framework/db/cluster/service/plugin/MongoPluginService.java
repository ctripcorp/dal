package com.ctrip.framework.db.cluster.service.plugin;

import com.ctrip.framework.db.cluster.domain.MongoClusterGetResponse;
import com.ctrip.framework.db.cluster.domain.PluginResponse;
import com.ctrip.framework.db.cluster.exception.DBClusterServiceException;
import com.ctrip.framework.db.cluster.service.config.ConfigService;
import com.ctrip.framework.db.cluster.util.HttpUtils;
import com.ctrip.framework.db.cluster.util.Utils;
import com.ctrip.framework.db.cluster.vo.mongo.MongoClusterVo;
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
public class MongoPluginService {

    private static final String ADD_OPERATOR = "/add";
    private static final String GET_OPERATOR = "/info";
    private static final String UPDATE_OPERATOR = "/update";

    @Autowired
    private ConfigService configService;

    public PluginResponse add(MongoClusterVo mongoClusterVo, String env, String subEnv, String operator) throws DBClusterServiceException {
        Transaction t = Cat.newTransaction("Plugin.Add.Mongo.Cluster", mongoClusterVo.getClusterName());
        PluginResponse pluginResponse;
        try {
            String url = configService.getPluginMongoUrl() + ADD_OPERATOR;
            pluginResponse = getResponse(mongoClusterVo, env, subEnv, operator, url);
            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            log.error("Add mongo cluster[" + mongoClusterVo.getClusterName() + "] to plugin encounter error.", e);
            t.setStatus(e);
            Cat.logError(e);
            throw new DBClusterServiceException(e);
        } finally {
            t.complete();
        }
        return pluginResponse;
    }

    public PluginResponse update(MongoClusterVo mongoClusterVo, String env, String subEnv, String operator) throws DBClusterServiceException {
        Transaction t = Cat.newTransaction("Plugin.Update.Mongo.Cluster", mongoClusterVo.getClusterName());
        PluginResponse pluginResponse;
        try {
            String url = configService.getPluginMongoUrl() + UPDATE_OPERATOR;
            pluginResponse = getResponse(mongoClusterVo, env, subEnv, operator, url);
            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            log.error("Update mongo cluster[" + mongoClusterVo.getClusterName() + "] to plugin encounter error.", e);
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
            String response = HttpUtils.getInstance().sendGet(url, urlParams);
            pluginResponse = Utils.gson.fromJson(response, MongoClusterGetResponse.class);
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

    private PluginResponse getResponse(MongoClusterVo mongoClusterVo, String env, String subEnv, String operator, String url) throws Exception {
        List<NameValuePair> urlParams = Lists.newArrayListWithCapacity(3);
        urlParams.add(new BasicNameValuePair("env", env));
        urlParams.add(new BasicNameValuePair("subenv", subEnv));
        urlParams.add(new BasicNameValuePair("operator", operator));
        String request = Utils.gson.toJson(mongoClusterVo);
        String response = HttpUtils.getInstance().sendPost(url, urlParams, request);
        PluginResponse pluginResponse = Utils.gson.fromJson(response, PluginResponse.class);
        return pluginResponse;
    }
}
