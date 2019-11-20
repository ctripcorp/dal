package com.ctrip.framework.db.cluster.service.plugin;

import com.ctrip.framework.db.cluster.domain.*;
import com.ctrip.framework.db.cluster.domain.plugin.dal.delete.DeleteCluster;
import com.ctrip.framework.db.cluster.domain.plugin.dal.release.ReleaseCluster;
import com.ctrip.framework.db.cluster.exception.DBClusterServiceException;
import com.ctrip.framework.db.cluster.service.config.ConfigService;
import com.ctrip.framework.db.cluster.util.Constants;
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

    private static final String delete = "/delete";

    private final ConfigService configService;


    public PluginResponse releaseClusters(final List<ReleaseCluster> clusters,
                                          final String operator) {
        Transaction t = Cat.newTransaction("Dal.Plugin.Release", "Release");
        PluginResponse pluginResponse;
        try {
            List<NameValuePair> urlParams = Lists.newArrayList();
            urlParams.add(new BasicNameValuePair("env", Constants.ENV));
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

    public PluginResponse deleteClusters(final List<DeleteCluster> clusters, final String operator) {
        Transaction t = Cat.newTransaction("Dal.Plugin.Delete", "Delete");
        PluginResponse pluginResponse;
        try {
            List<NameValuePair> urlParams = Lists.newArrayList();
            urlParams.add(new BasicNameValuePair("env", Constants.ENV));
            urlParams.add(new BasicNameValuePair("operator", operator));
            String url = configService.getPluginDalUrl() + delete;
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
}
