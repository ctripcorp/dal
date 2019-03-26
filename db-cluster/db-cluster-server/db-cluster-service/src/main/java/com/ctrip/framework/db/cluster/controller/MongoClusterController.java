package com.ctrip.framework.db.cluster.controller;

import com.ctrip.framework.db.cluster.domain.MongoAddRequestBody;
import com.ctrip.framework.db.cluster.domain.ResponseModel;
import com.ctrip.framework.db.cluster.domain.TitanAddRequestBody;
import com.ctrip.framework.db.cluster.domain.TitanAddResponseBody;
import com.ctrip.framework.db.cluster.enums.ResponseStatus;
import com.ctrip.framework.db.cluster.service.TitanSyncService;
import com.ctrip.framework.db.cluster.util.IpUtil;
import com.ctrip.framework.db.cluster.util.ValidityChecker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseModel add(@RequestBody MongoAddRequestBody requestBody, HttpServletRequest request) {
        try {
            String requestIp = IpUtil.getRequestIp(request);
            if (!ValidityChecker.checkAllowedIp(requestIp)) {
                return ResponseModel.forbiddenResponse();
            }

            return ResponseModel.successResponse(requestBody);
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
