package com.ctrip.framework.db.cluster.controller;

import com.ctrip.framework.db.cluster.config.ConfigService;
import com.ctrip.framework.db.cluster.crypto.CipherService;
import com.ctrip.framework.db.cluster.domain.MongoCluster;
import com.ctrip.framework.db.cluster.domain.MongoClusterGetResponse;
import com.ctrip.framework.db.cluster.domain.PluginResponse;
import com.ctrip.framework.db.cluster.domain.ResponseModel;
import com.ctrip.framework.db.cluster.enums.ResponseStatus;
import com.ctrip.framework.db.cluster.service.PluginMongoService;
import com.ctrip.framework.db.cluster.util.IpUtil;
import com.ctrip.framework.db.cluster.util.ValidityChecker;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
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

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResponseModel update(@RequestBody MongoCluster mongoCluster,
                                @RequestParam(name = "env", required = false) String env,
                                @RequestParam(name = "operator", required = false) String operator,
                                HttpServletRequest request) {
        try {
            Preconditions.checkArgument(StringUtils.isNotBlank(env), "env为空");
            Preconditions.checkArgument(StringUtils.isNotBlank(operator), "operator为空");
            if (!validityChecker.checkAllowedIp(IpUtil.getRequestIp(request), configService.getAllowedIps())) {
                return ResponseModel.forbiddenResponse();
            }

            Preconditions.checkNotNull(mongoCluster, "Cluster信息为空");
            String clusterName = mongoCluster.getClusterName();
            Preconditions.checkArgument(StringUtils.isNotBlank(clusterName), "clusterName为空");
            // todo:校验cluster

            PluginResponse response = pluginMongoService.update(mongoCluster, env, operator);
            if (response.getStatus() == 0) {
                return ResponseModel.successResponse(response.getData());
            } else {
                return ResponseModel.failResponse(ResponseStatus.BAD_REQUEST, response.getMessage());
            }

        } catch (NullPointerException e) {
            return ResponseModel.failResponse(ResponseStatus.BAD_REQUEST, e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseModel.failResponse(ResponseStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Update mongo cluster info failed.", e);
            return ResponseModel.failResponse(ResponseStatus.ERROR, e.getMessage());
        }
    }

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public ResponseModel get(@RequestParam(name = "clustername", required = false) String clusterName,
                             @RequestParam(name = "env", required = false) String env,
                             HttpServletRequest request) {
        try {
            Preconditions.checkArgument(StringUtils.isNotBlank(clusterName), "clustername为空");
            Preconditions.checkArgument(StringUtils.isNotBlank(env), "env为空");
            if (!validityChecker.checkAllowedIp(IpUtil.getRequestIp(request), configService.getAllowedIps())) {
                return ResponseModel.forbiddenResponse();
            }

            MongoClusterGetResponse response = pluginMongoService.get(clusterName, env);
            if (response.getStatus() == 0) {
                return ResponseModel.successResponse(response.getData());
            } else {
                return ResponseModel.failResponse(ResponseStatus.BAD_REQUEST, response.getMessage());
            }

        } catch (IllegalArgumentException e) {
            return ResponseModel.failResponse(ResponseStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Get mongo cluster info failed.", e);
            return ResponseModel.failResponse(ResponseStatus.ERROR, e.getMessage());
        }
    }

}
