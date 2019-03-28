package com.ctrip.framework.db.cluster.controller;

import com.ctrip.framework.db.cluster.config.ConfigService;
import com.ctrip.framework.db.cluster.crypto.CipherService;
import com.ctrip.framework.db.cluster.domain.MongoCluster;
import com.ctrip.framework.db.cluster.domain.ResponseModel;
import com.ctrip.framework.db.cluster.domain.TitanAddRequest;
import com.ctrip.framework.db.cluster.domain.TitanAddResponse;
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
    @Autowired
    private ConfigService configService;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseModel add(@RequestBody MongoCluster mongoCluster,
                             @RequestParam(name = "env", required = false) String env,
                             @RequestParam(name = "operator", required = false) String operator, HttpServletRequest request) {
        try {
            if (!ValidityChecker.checkAllowedIp(IpUtil.getRequestIp(request), configService.getAllowedIps())) {
                return ResponseModel.forbiddenResponse();
            }

            env = ValidityChecker.checkAndGetEnv(env);
            ValidityChecker.checkOperator(operator);

            ValidityChecker.checkMongoCluster(mongoCluster);

            // 加密用户名和密码
            String userId = cipherService.encrypt(mongoCluster.getUserId());
            String password = cipherService.encrypt(mongoCluster.getPassword());
            mongoCluster.setUserId(userId);
            mongoCluster.setPassword(password);

            Cat.logEvent("DB.Cluster.Service.Mongo.Cluster.Add", String.format("%s:%s:%s", mongoCluster.getClusterName(), env, operator));
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
    public ResponseModel addTitan(@RequestBody TitanAddRequest requestBody, HttpServletRequest request) {
        try {
            String requestIp = IpUtil.getRequestIp(request);
            if (!ValidityChecker.checkAllowedIp(requestIp, configService.getAllowedIps())) {
                return ResponseModel.forbiddenResponse();
            }

            TitanAddResponse titanAddResponse = titanSyncService.add(requestBody);

            return ResponseModel.successResponse(titanAddResponse);
        } catch (Exception e) {
            log.error("Sync titan key info failed.", e);
            return ResponseModel.failResponse(ResponseStatus.ERROR, e.getMessage());
        }
    }
}
