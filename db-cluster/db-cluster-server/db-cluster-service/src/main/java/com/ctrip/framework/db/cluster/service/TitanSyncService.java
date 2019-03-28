package com.ctrip.framework.db.cluster.service;

import com.ctrip.framework.db.cluster.config.ConfigService;
import com.ctrip.framework.db.cluster.domain.TitanAddRequest;
import com.ctrip.framework.db.cluster.domain.TitanAddResponse;
import com.ctrip.framework.db.cluster.exception.DBClusterServiceException;
import com.ctrip.framework.db.cluster.util.HttpUtil;
import com.ctrip.framework.db.cluster.util.Util;
import com.ctrip.framework.foundation.Foundation;
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
 * Created by shenjie on 2019/3/18.
 */
@Slf4j
@Service
public class TitanSyncService {

    @Autowired
    private ConfigService configService;

    public TitanAddResponse add(TitanAddRequest titanAddRequest) throws DBClusterServiceException {
        Transaction t = Cat.newTransaction("DAL.Cluster.Server.Titan.Plugin.Add", titanAddRequest.getKeyName());
        TitanAddResponse titanAddResponse;
        try {
            List<NameValuePair> urlParams = Lists.newArrayListWithCapacity(2);
            urlParams.add(new BasicNameValuePair("titankey", titanAddRequest.getKeyName()));
            urlParams.add(new BasicNameValuePair("env", Foundation.server().getEnvFamily().getName()));
            String request = Util.gson.toJson(titanAddRequest);
            String response = HttpUtil.getInstance().sendPost(configService.getTitanPluginUrl(), urlParams, request);
            titanAddResponse = Util.gson.fromJson(response, TitanAddResponse.class);
            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            log.error("Add titan key[" + titanAddRequest.getKeyName() + "] to titan plugin encounter error.", e);
            t.setStatus(e);
            Cat.logError(e);
            throw new DBClusterServiceException(e);
        } finally {
            t.complete();
        }
        return titanAddResponse;
    }

}
