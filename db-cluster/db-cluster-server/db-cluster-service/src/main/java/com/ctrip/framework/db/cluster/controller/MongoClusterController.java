package com.ctrip.framework.db.cluster.controller;

import com.ctrip.framework.db.cluster.config.ConfigService;
import com.ctrip.framework.db.cluster.crypto.CipherService;
import com.ctrip.framework.db.cluster.domain.MongoCluster;
import com.ctrip.framework.db.cluster.domain.PluginResponse;
import com.ctrip.framework.db.cluster.domain.ResponseModel;
import com.ctrip.framework.db.cluster.domain.TitanAddRequest;
import com.ctrip.framework.db.cluster.enums.ResponseStatus;
import com.ctrip.framework.db.cluster.service.PluginMongoService;
import com.ctrip.framework.db.cluster.service.PluginTitanService;
import com.ctrip.framework.db.cluster.util.IpUtil;
import com.ctrip.framework.db.cluster.util.ValidityChecker;
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
    private PluginTitanService pluginTitanService;
    @Autowired
    private PluginMongoService pluginMongoService;
    @Autowired
    private CipherService cipherService;
    @Autowired
    private ConfigService configService;
    @Autowired
    private ValidityChecker validityChecker;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseModel add(@RequestBody MongoCluster mongoCluster,
                             @RequestParam(name = "env", required = false) String env,
                             @RequestParam(name = "operator", required = false) String operator,
                             HttpServletRequest request) {
        try {
            if (!validityChecker.checkAllowedIp(IpUtil.getRequestIp(request), configService.getAllowedIps())) {
                return ResponseModel.forbiddenResponse();
            }

            env = validityChecker.checkAndGetEnv(env);
            validityChecker.checkOperator(operator);

            validityChecker.checkMongoCluster(mongoCluster);

            // 加密用户名和密码
//            String userId = cipherService.encrypt(mongoCluster.getUserId());
//            String password = cipherService.encrypt(mongoCluster.getPassword());
//            mongoCluster.setUserId(userId);
//            mongoCluster.setPassword(password);

            // 新增cluster，version=1
            mongoCluster.setVersion(1);
            mongoCluster.setBu(null);
            mongoCluster.setProdLine(null);
            mongoCluster.setContacts(null);

            PluginResponse response = pluginMongoService.add(mongoCluster, env, operator);
            if (response.getStatus() == 0) {
                return ResponseModel.successResponse();
            } else {
                return ResponseModel.failResponse(ResponseStatus.BAD_REQUEST, response.getMessage());
            }

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
    public ResponseModel addTitan(@RequestBody TitanAddRequest requestBody,
                                  @RequestParam(name = "env", required = false) String env,
                                  HttpServletRequest request) {
        try {
            String requestIp = IpUtil.getRequestIp(request);
            if (!validityChecker.checkAllowedIp(requestIp, configService.getAllowedIps())) {
                return ResponseModel.forbiddenResponse();
            }

            env = validityChecker.checkAndGetEnv(env);

            PluginResponse response = pluginTitanService.add(requestBody, env);
            if (response.getStatus() == 0) {
                return ResponseModel.successResponse();
            } else {
                return ResponseModel.failResponse(ResponseStatus.BAD_REQUEST, response.getMessage());
            }

        } catch (Exception e) {
            log.error("Add titan key failed.", e);
            return ResponseModel.failResponse(ResponseStatus.ERROR, e.getMessage());
        }
    }
}
