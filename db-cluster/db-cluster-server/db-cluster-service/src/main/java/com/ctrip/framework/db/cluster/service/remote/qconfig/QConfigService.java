package com.ctrip.framework.db.cluster.service.remote.qconfig;

import com.ctrip.framework.db.cluster.exception.DBClusterServiceException;
import com.ctrip.framework.db.cluster.service.config.ConfigService;
import com.ctrip.framework.db.cluster.service.remote.qconfig.domain.QConfigFileDetailResponse;
import com.ctrip.framework.db.cluster.service.remote.qconfig.domain.QConfigFileNameResponse;
import com.ctrip.framework.db.cluster.service.remote.qconfig.domain.QConfigSubEnvResponse;
import com.ctrip.framework.db.cluster.util.Constants;
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

/**
 * Created by @author zhuYongMing on 2019/11/7.
 * http://conf.ctripcorp.com/pages/viewpage.action?pageId=177055970#id-1.QConfig%E7%94%A8%E6%88%B7%E4%BD%BF%E7%94%A8%E6%89%8B%E5%86%8C-5.4.3%E8%8E%B7%E5%8F%96%E9%85%8D%E7%BD%AE%E5%88%97%E8%A1%A8
 */
@Slf4j
@Service
@AllArgsConstructor
public class QConfigService {

    private static final String file_name = "/configs/list";

    private static final String sub_env = "/envs/%s/subenvs/" + Constants.TITAN_PLUGIN_APPID;

    private static final String file_detail = "/configs";

    private final ConfigService configService;


    public QConfigFileNameResponse queryFileNames(final String subEnv) {
        Transaction t = Cat.newTransaction("QConfig.RestApi.Query.FileNames", "FileNames");
        try {
            List<NameValuePair> urlParams = Lists.newArrayListWithCapacity(5);
            urlParams.add(new BasicNameValuePair("token", configService.getQConfigRestApiToken()));
            urlParams.add(new BasicNameValuePair("groupid", Constants.DAL_CLUSTER_SERVICE_APPID));
            urlParams.add(new BasicNameValuePair("targetgroupid", Constants.TITAN_PLUGIN_APPID));
            urlParams.add(new BasicNameValuePair("targetenv", Constants.ENV));
            urlParams.add(new BasicNameValuePair("targetsubenv", subEnv));

            final String url = configService.getQConfigRestApiUrl() + file_name;
            String response = HttpUtils.getInstance().sendGet(url, urlParams, configService.getHttpReadTimeoutInMs());
            QConfigFileNameResponse fileNamesResponse = Utils.gson.fromJson(response, QConfigFileNameResponse.class);
            t.setStatus(Message.SUCCESS);

            return fileNamesResponse;
        } catch (Exception e) {
            t.setStatus(e);
            Cat.logError(e);
            throw new DBClusterServiceException(e);
        } finally {
            t.complete();
        }
    }

    public QConfigSubEnvResponse querySubEnv(final String env) {
        Transaction t = Cat.newTransaction("QConfig.RestApi.Query.SubEnv", "SubEnv");
        try {
            List<NameValuePair> urlParams = Lists.newArrayListWithCapacity(2);
            urlParams.add(new BasicNameValuePair("token", configService.getQConfigRestApiToken()));
            urlParams.add(new BasicNameValuePair("groupid", Constants.DAL_CLUSTER_SERVICE_APPID));

            final String url = configService.getQConfigRestApiUrl() + String.format(sub_env, env.toLowerCase());
            String response = HttpUtils.getInstance().sendGet(url, urlParams, configService.getHttpReadTimeoutInMs());
            QConfigSubEnvResponse subEnvResponse = Utils.gson.fromJson(response, QConfigSubEnvResponse.class);
            t.setStatus(Message.SUCCESS);

            return subEnvResponse;
        } catch (Exception e) {
            t.setStatus(e);
            Cat.logError(e);
            throw new DBClusterServiceException(e);
        } finally {
            t.complete();
        }
    }

    public QConfigFileDetailResponse queryFileDetail(final String name, final String subEnv) {
        Transaction t = Cat.newTransaction("QConfig.RestApi.Query.FileDetail", name);
        try {
            List<NameValuePair> urlParams = Lists.newArrayListWithCapacity(6);
            urlParams.add(new BasicNameValuePair("token", configService.getQConfigRestApiToken()));
            urlParams.add(new BasicNameValuePair("groupid", Constants.DAL_CLUSTER_SERVICE_APPID));
            urlParams.add(new BasicNameValuePair("dataid", name));
            urlParams.add(new BasicNameValuePair("env", Constants.ENV));
            urlParams.add(new BasicNameValuePair("subenv", subEnv));
            urlParams.add(new BasicNameValuePair("targetgroupid", Constants.TITAN_PLUGIN_APPID));

            final String url = configService.getQConfigRestApiUrl() + file_detail;
            String response = HttpUtils.getInstance().sendGet(url, urlParams, configService.getHttpReadTimeoutInMs());
            QConfigFileDetailResponse detailResponse = Utils.gson.fromJson(response, QConfigFileDetailResponse.class);
            t.setStatus(Message.SUCCESS);

            return detailResponse;
        } catch (Exception e) {
            t.setStatus(e);
            Cat.logError(e);
            throw new DBClusterServiceException(e);
        } finally {
            t.complete();
        }
    }
}
