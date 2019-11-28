package com.ctrip.framework.db.cluster.controller;

import com.ctrip.framework.db.cluster.crypto.CipherService;
import com.ctrip.framework.db.cluster.domain.MongoClusterGetResponse;
import com.ctrip.framework.db.cluster.domain.PluginResponse;
import com.ctrip.framework.db.cluster.vo.ResponseStatus;
import com.ctrip.framework.db.cluster.service.config.ConfigService;
import com.ctrip.framework.db.cluster.service.plugin.MongoPluginService;
import com.ctrip.framework.db.cluster.util.Constants;
import com.ctrip.framework.db.cluster.util.IpUtils;
import com.ctrip.framework.db.cluster.util.MongoValidityChecker;
import com.ctrip.framework.db.cluster.vo.ResponseModel;
import com.ctrip.framework.db.cluster.vo.mongo.MongoClusterVo;
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
    private MongoPluginService mongoPluginService;
    @Autowired
    private CipherService cipherService;
    @Autowired
    private ConfigService configService;
    @Autowired
    private MongoValidityChecker mongoValidityChecker;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseModel add(@RequestBody MongoClusterVo mongoClusterVo,
                             @RequestParam(name = "env", required = false, defaultValue = Constants.ENV_PRO) String env,
                             @RequestParam(name = "subenv", required = false) String subEnv,
                             @RequestParam(name = "operator", required = false) String operator,
                             HttpServletRequest request) {
        try {
            Preconditions.checkArgument(StringUtils.isNotBlank(operator), "operator为空");
            if (!IpUtils.checkAllowedIp(request, configService.getAllowedIps())) {
                return ResponseModel.forbiddenResponse();
            }

            mongoValidityChecker.checkMongoCluster(mongoClusterVo);

            // 加密用户名和密码
//            String userId = cipherService.encrypt(mongoCluster.getUserId());
//            String password = cipherService.encrypt(mongoCluster.getPassword());
//            mongoCluster.setUserId(userId);
//            mongoCluster.setPassword(password);

            // 新增cluster，version=1
            mongoClusterVo.setVersion(1);
            mongoClusterVo.setBu(null);
            mongoClusterVo.setProdLine(null);
            mongoClusterVo.setContacts(null);

            PluginResponse response = mongoPluginService.add(mongoClusterVo, env, subEnv, operator);

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
    public ResponseModel update(@RequestBody MongoClusterVo mongoClusterVo,
                                @RequestParam(name = "env", required = false) String env,
                                @RequestParam(name = "subenv", required = false) String subEnv,
                                @RequestParam(name = "operator", required = false) String operator,
                                HttpServletRequest request) {
        try {
            Preconditions.checkArgument(StringUtils.isNotBlank(env), "env为空");
            Preconditions.checkArgument(StringUtils.isNotBlank(operator), "operator为空");
            if (!IpUtils.checkAllowedIp(request, configService.getAllowedIps())) {
                return ResponseModel.forbiddenResponse();
            }

            Preconditions.checkNotNull(mongoClusterVo, "Cluster信息为空");
            String clusterName = mongoClusterVo.getClusterName();
            Preconditions.checkArgument(StringUtils.isNotBlank(clusterName), "clusterName为空");
            // todo:校验cluster

            PluginResponse response = mongoPluginService.update(mongoClusterVo, env, subEnv, operator);

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
                             @RequestParam(name = "subenv", required = false) String subEnv,
                             HttpServletRequest request) {
        try {
            Preconditions.checkArgument(StringUtils.isNotBlank(clusterName), "clustername为空");
            Preconditions.checkArgument(StringUtils.isNotBlank(env), "env为空");
            if (!IpUtils.checkAllowedIp(request, configService.getAllowedIps())) {
                return ResponseModel.forbiddenResponse();
            }

            MongoClusterGetResponse response = mongoPluginService.get(clusterName, env, subEnv);

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
