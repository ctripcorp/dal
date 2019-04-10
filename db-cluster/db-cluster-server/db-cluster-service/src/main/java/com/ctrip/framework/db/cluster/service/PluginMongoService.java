package com.ctrip.framework.db.cluster.service;

import com.ctrip.framework.db.cluster.config.ConfigService;
import com.ctrip.framework.db.cluster.domain.MongoCluster;
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

import static com.ctrip.framework.db.cluster.util.Constants.MONGO_CLIENT_APPID;

/**
 * Created by shenjie on 2019/4/2.
 */
@Slf4j
@Service
public class PluginMongoService {

    @Autowired
    private ConfigService configService;

    public PluginResponse add(MongoCluster mongoCluster, String env, String operator) throws DBClusterServiceException {
        Transaction t = Cat.newTransaction("Plugin.Add.Mongo.Cluster", mongoCluster.getClusterName());
        PluginResponse pluginResponse;
        try {
            List<NameValuePair> urlParams = Lists.newArrayListWithCapacity(2);
            urlParams.add(new BasicNameValuePair("env", env));
            urlParams.add(new BasicNameValuePair("operator", operator));
            String request = Util.gson.toJson(mongoCluster);
            String response = HttpUtil.getInstance().sendPost(configService.getPluginMongoUrl(), urlParams, request);
            pluginResponse = Util.gson.fromJson(response, PluginResponse.class);
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
}
