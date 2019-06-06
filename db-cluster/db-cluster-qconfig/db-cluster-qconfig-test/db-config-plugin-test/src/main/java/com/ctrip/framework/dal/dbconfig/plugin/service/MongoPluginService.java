package com.ctrip.framework.dal.dbconfig.plugin.service;

import com.ctrip.framework.dal.dbconfig.plugin.entity.MongoClusterEntity;
import com.ctrip.framework.dal.dbconfig.plugin.entity.MongoClusterGetResponse;
import com.ctrip.framework.dal.dbconfig.plugin.entity.PluginResponse;
import com.ctrip.framework.dal.dbconfig.plugin.util.HttpUtils;
import com.ctrip.framework.dal.dbconfig.plugin.util.Utils;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by shenjie on 2019/4/10.
 */
@Slf4j
@Service
public class MongoPluginService {
    //    public static final String MONGO_PLUGIN_URL = "http://qconfig.ctripcorp.com/plugins/mongo/config";
//    public static final String MONGO_PLUGIN_URL = "http://qconfig.fat16.qa.nt.ctripcorp.com/plugins/mongo/config";
    public static final String MONGO_PLUGIN_URL = "http://localhost:8082/plugins/mongo/config";
//    public static final String MONGO_PLUGIN_URL = "http://qconfig2.fat1.qa.nt.ctripcorp.com/plugins/mongo/config";

    public static final String ADD_CLUSTER_URL = MONGO_PLUGIN_URL + "/add";
    public static final String UPDATE_CLUSTER_URL = MONGO_PLUGIN_URL + "/update";
    public static final String GET_CLUSTER_URL = MONGO_PLUGIN_URL + "/info";

    public PluginResponse addMongoCluster(MongoClusterEntity clusterEntity, String env, String operator) {
        Transaction t = Cat.newTransaction("Mongo.Plugin", "addMongoCluster");
        PluginResponse pluginResponse = null;
        try {
            List<NameValuePair> urlParams = Lists.newArrayListWithCapacity(2);
            urlParams.add(new BasicNameValuePair("env", env));
            urlParams.add(new BasicNameValuePair("operator", operator));
            String request = Utils.gson.toJson(clusterEntity);
            String response = HttpUtils.getInstance().sendPost(ADD_CLUSTER_URL, urlParams, request);
            pluginResponse = Utils.gson.fromJson(response, PluginResponse.class);
            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            log.error("Add cluster[" + clusterEntity.getClusterName() + "] to plugin encounter error.", e);
            t.setStatus(e);
            Cat.logError(e);
        } finally {
            t.complete();
        }
        return pluginResponse;
    }

    public PluginResponse updateMongoCluster(MongoClusterEntity clusterEntity, String env, String operator) {
        Transaction t = Cat.newTransaction("Mongo.Plugin", "updateMongoCluster");
        PluginResponse pluginResponse = null;
        try {
            List<NameValuePair> urlParams = Lists.newArrayListWithCapacity(2);
            urlParams.add(new BasicNameValuePair("env", env));
            urlParams.add(new BasicNameValuePair("operator", operator));
            String request = Utils.gson.toJson(clusterEntity);
            String response = HttpUtils.getInstance().sendPost(UPDATE_CLUSTER_URL, urlParams, request);
            pluginResponse = Utils.gson.fromJson(response, PluginResponse.class);
            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            log.error("Update cluster[" + clusterEntity.getClusterName() + "] to plugin encounter error.", e);
            t.setStatus(e);
            Cat.logError(e);
        } finally {
            t.complete();
        }
        return pluginResponse;
    }

    public MongoClusterGetResponse getMongoCluster(String clusterName, String env) {
        Transaction t = Cat.newTransaction("Mongo.Plugin", "getMongoCluster");
        MongoClusterGetResponse pluginResponse = null;
        try {
            List<NameValuePair> urlParams = Lists.newArrayListWithCapacity(2);
            urlParams.add(new BasicNameValuePair("clustername", clusterName));
            urlParams.add(new BasicNameValuePair("env", env));
            String response = HttpUtils.getInstance().sendGet(GET_CLUSTER_URL, urlParams);
            pluginResponse = Utils.gson.fromJson(response, MongoClusterGetResponse.class);
            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            log.error("Get cluster[" + clusterName + "] from plugin encounter error.", e);
            t.setStatus(e);
            Cat.logError(e);
        } finally {
            t.complete();
        }
        return pluginResponse;
    }

}
