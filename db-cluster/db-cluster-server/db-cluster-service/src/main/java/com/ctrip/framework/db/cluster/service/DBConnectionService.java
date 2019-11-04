package com.ctrip.framework.db.cluster.service;

import com.ctrip.framework.db.cluster.domain.dba.connect.DBConnectionCheckRequest;
import com.ctrip.framework.db.cluster.domain.dba.connect.DBConnectionCheckResponse;
import com.ctrip.framework.db.cluster.exception.DBClusterServiceException;
import com.ctrip.framework.db.cluster.service.config.ConfigService;
import com.ctrip.framework.db.cluster.util.HttpUtils;
import com.ctrip.framework.db.cluster.util.Utils;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Created by shenjie on 2019/5/8.
 */
@Slf4j
@Service
@AllArgsConstructor
public class DBConnectionService {

    private final ConfigService configService;


    private DBConnectionCheckResponse check(DBConnectionCheckRequest connectionCheckRequest) {
        Transaction t = Cat.newTransaction("DB.Cluster.Service.DBConnectionCheck", connectionCheckRequest.getDbName());
        DBConnectionCheckResponse checkResponse;
        try {
            String requestBody = Utils.gson.toJson(connectionCheckRequest);
            // log request
            connectionCheckRequest.setPassword("******");
            log.info("DBConnectionCheckRequest:" + connectionCheckRequest.toString());

            String responseBody = HttpUtils.getInstance().sendPost(configService.getDBConnectionCheckUrl(), requestBody);
            checkResponse = Utils.gson.fromJson(responseBody, DBConnectionCheckResponse.class);
            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            log.error("Check db[" + connectionCheckRequest.getDbName() + "] connection encounter error.", e);
            t.setStatus(e);
            Cat.logError(e);
            throw new DBClusterServiceException(e);
        } finally {
            t.complete();
        }
        return checkResponse;
    }

    public boolean checkConnection(DBConnectionCheckRequest connectionCheckRequest) {
        boolean isValid = false;
        DBConnectionCheckResponse connectionCheckResponse = check(connectionCheckRequest);
        if (connectionCheckResponse != null) {
            isValid = connectionCheckResponse.isSuccess();
            if (!isValid) {
                String errMsg = connectionCheckResponse.getMessage();
                Cat.logEvent("DB.Cluster.Service.DBConnectionCheck.Failed", connectionCheckRequest.getDbName(), Event.SUCCESS, errMsg);
            }
        }
        return isValid;
    }
}
