package com.ctrip.framework.db.cluster.controller;

import com.ctrip.framework.db.cluster.vo.ResponseModel;
import com.ctrip.framework.db.cluster.vo.dal.create.ClusterExtensionConfigVo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * Created by @author zhuYongMing on 2019/11/5.
 */
@Slf4j
@RestController
@RequestMapping("api/dal/v1")
@AllArgsConstructor
public class ClusterExtensionConfigController {

    @PostMapping(value = "/clusters/{clusterName}/extensionConfigs")
    public ResponseModel createExtensionConfig(@PathVariable String clusterName,
                                               @RequestParam(name = "operator") String operator,
                                               @RequestBody ClusterExtensionConfigVo[] configs) {

        ResponseModel response = ResponseModel.successResponse();
        response.setMessage("delete shards success");
        return response;
    }
}
