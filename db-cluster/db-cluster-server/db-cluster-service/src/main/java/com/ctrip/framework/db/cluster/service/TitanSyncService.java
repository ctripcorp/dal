package com.ctrip.framework.db.cluster.service;

import com.ctrip.framework.db.cluster.config.ConfigManager;
import com.ctrip.framework.db.cluster.domain.TitanAddRequestBody;
import com.ctrip.framework.db.cluster.domain.TitanAddResponseBody;
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
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by shenjie on 2019/3/18.
 */
@Slf4j
@Service
public class TitanSyncService {

    public TitanAddResponseBody add(TitanAddRequestBody titanAddRequestBody) throws DBClusterServiceException {
        Transaction t = Cat.newTransaction("DAL.Cluster.Server.Titan.Plugin.Add", titanAddRequestBody.getKeyName());
        TitanAddResponseBody titanAddResponseBody;
        try {
            List<NameValuePair> urlParams = Lists.newArrayListWithCapacity(2);
            urlParams.add(new BasicNameValuePair("titankey", titanAddRequestBody.getKeyName()));
            urlParams.add(new BasicNameValuePair("env", Foundation.server().getEnvFamily().getName()));
            String request = Util.gson.toJson(titanAddRequestBody);
            String response = HttpUtil.getInstance().sendPost(ConfigManager.getInstance().getTitanPluginUrl(), urlParams, request);
            titanAddResponseBody = Util.gson.fromJson(response, TitanAddResponseBody.class);
            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            log.error("Add titan key[" + titanAddRequestBody.getKeyName() + "] to titan plugin encounter error.", e);
            t.setStatus(e);
            Cat.logError(e);
            throw new DBClusterServiceException(e);
        } finally {
            t.complete();
        }
        return titanAddResponseBody;
    }

}
