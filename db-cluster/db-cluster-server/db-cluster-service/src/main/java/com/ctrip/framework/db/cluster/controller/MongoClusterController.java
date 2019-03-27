package com.ctrip.framework.db.cluster.controller;

import com.ctrip.framework.db.cluster.crypto.CipherService;
import com.ctrip.framework.db.cluster.domain.MongoClusterAddRequestBody;
import com.ctrip.framework.db.cluster.domain.ResponseModel;
import com.ctrip.framework.db.cluster.domain.TitanAddRequestBody;
import com.ctrip.framework.db.cluster.domain.TitanAddResponseBody;
import com.ctrip.framework.db.cluster.enums.ResponseStatus;
import com.ctrip.framework.db.cluster.service.TitanSyncService;
import com.ctrip.framework.db.cluster.util.IpUtil;
import com.ctrip.framework.db.cluster.util.ValidityChecker;
import com.dianping.cat.Cat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by shenjie on 2019/3/26.
 */
@Slf4j
@RestController
@RequestMapping("mongo/cluster")
public class MongoClusterController {

    @Autowired
    private TitanSyncService titanSyncService;
    @Autowired
    private CipherService cipherService;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseModel add(@RequestBody MongoClusterAddRequestBody requestBody,
                             @RequestParam(name = "env", required = false) String env,
                             @RequestParam(name = "operator", required = false) String operator, HttpServletRequest request) {
        try {
            if (!ValidityChecker.checkAllowedIp(IpUtil.getRequestIp(request))) {
                return ResponseModel.forbiddenResponse();
            }

            env = ValidityChecker.checkAndGetEnv(env);
            ValidityChecker.checkOperator(operator);

            ValidityChecker.checkMongoCluster(requestBody);

            // 加密用户名和密码
            String userId = cipherService.encrypt(requestBody.getUserId());
            String password = cipherService.encrypt(requestBody.getPassword());
            requestBody.setUserId(userId);
            requestBody.setPassword(password);

            Cat.logEvent("DB.Cluster.Service.Mongo.Cluster.Add", String.format("%s:%s:%s", requestBody.getClusterName(), env, operator));
            return ResponseModel.successResponse();
        } catch (NullPointerException e) {
            return ResponseModel.failResponse(ResponseStatus.BAD_REQUEST, e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseModel.failResponse(ResponseStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Add mongo cluster info failed.", e);
            return ResponseModel.failResponse(ResponseStatus.ERROR, e.getMessage());
        }
    }

    @RequestMapping(value = "/titan/add", method = RequestMethod.POST)
    public ResponseModel addTitan(@RequestBody TitanAddRequestBody requestBody, HttpServletRequest request) {
        try {
            String requestIp = IpUtil.getRequestIp(request);
            if (!ValidityChecker.checkAllowedIp(requestIp)) {
                return ResponseModel.forbiddenResponse();
            }

            TitanAddResponseBody titanAddResponseBody = titanSyncService.add(requestBody);

            return ResponseModel.successResponse(titanAddResponseBody);
        } catch (Exception e) {
            log.error("Sync titan key info failed.", e);
            return ResponseModel.failResponse(ResponseStatus.ERROR, e.getMessage());
        }
    }
}
