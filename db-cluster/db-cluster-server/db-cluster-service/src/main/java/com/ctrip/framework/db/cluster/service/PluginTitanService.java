package com.ctrip.framework.db.cluster.service;

import com.ctrip.framework.db.cluster.config.ConfigService;
import com.ctrip.framework.db.cluster.domain.PluginResponse;
import com.ctrip.framework.db.cluster.domain.TitanAddRequest;
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

import static com.ctrip.framework.db.cluster.util.Constants.TITAN_PLUGIN_APPID;

/**
 * Created by shenjie on 2019/3/18.
 */
@Slf4j
@Service
public class PluginTitanService {

    @Autowired
    private ConfigService configService;

    public PluginResponse add(TitanAddRequest titanAddRequest, String env) throws DBClusterServiceException {
        Transaction t = Cat.newTransaction("Plugin.Add.TitanKey", titanAddRequest.getKeyName());
        PluginResponse pluginResponse;
        try {
            List<NameValuePair> urlParams = Lists.newArrayListWithCapacity(3);
            urlParams.add(new BasicNameValuePair("appid", TITAN_PLUGIN_APPID));
            urlParams.add(new BasicNameValuePair("titankey", titanAddRequest.getKeyName()));
            urlParams.add(new BasicNameValuePair("env", env));
            String request = Util.gson.toJson(titanAddRequest);
            String response = HttpUtil.getInstance().sendPost(configService.getPluginTitanUrl(), urlParams, request);
            pluginResponse = Util.gson.fromJson(response, PluginResponse.class);
            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            log.error("Add titan key[" + titanAddRequest.getKeyName() + "] to plugin encounter error.", e);
            t.setStatus(e);
            Cat.logError(e);
            throw new DBClusterServiceException(e);
        } finally {
            t.complete();
        }
        return pluginResponse;
    }

}
