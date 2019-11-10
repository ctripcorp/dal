package com.ctrip.framework.db.cluster.controller;

import com.ctrip.framework.db.cluster.domain.dto.ClusterDTO;
import com.ctrip.framework.db.cluster.enums.ResponseStatus;
import com.ctrip.framework.db.cluster.service.checker.SiteAccessChecker;
import com.ctrip.framework.db.cluster.service.repository.ClusterService;
import com.ctrip.framework.db.cluster.service.repository.UserService;
import com.ctrip.framework.db.cluster.vo.ResponseModel;
import com.ctrip.framework.db.cluster.vo.dal.create.UserVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by shenjie on 2019/3/14.
 */
@Slf4j
@RestController
@RequestMapping("api/dal/v1")
@RequiredArgsConstructor
public class UserController {

    private final SiteAccessChecker siteAccessChecker;

    private final UserService userService;

    private final ClusterService clusterService;

    @PostMapping(value = "/clusters/{clusterName}/shards/{shardIndex}/users")
    public ResponseModel createUsers(@PathVariable(name = "clusterName") final String clusterName,
                                     @PathVariable(name = "shardIndex") final int shardIndex,
                                     @RequestBody final List<UserVo> userVos,
                                     @RequestParam(name = "operator") final String operator,
                                     final HttpServletRequest request) {
        try {
            // access check
            if (!siteAccessChecker.isAllowed(request)) {
                return ResponseModel.forbiddenResponse();
            }

            final ClusterDTO clusterDTO = clusterService.findUnDeletedClusterDTO(clusterName);
            if (null == clusterDTO) {
                ResponseModel response = ResponseModel.successResponse();
                response.setMessage(String.format("cluster not exists, clusterName = %s", clusterName));
                return response;
            }

            userService.addUsers(shardIndex, userVos, clusterDTO);

            ResponseModel response = ResponseModel.successResponse();
            response.setMessage("Create users success");
            return response;
        } catch (Throwable t) {
            log.error(String.format("create users failed, cluster: %s, shard: %s", clusterName, shardIndex), t);
            return ResponseModel.failResponse(ResponseStatus.ERROR, t.getMessage());
        }
    }

//    @RequestMapping(value = "/update", method = RequestMethod.POST)
//    public ResponseModel update() {
//        return null;
//    }
//
//    @RequestMapping(value = "/query", method = RequestMethod.GET)
//    public ResponseModel query(@RequestParam(name = "name") long name) {
//        return null;
//    }
//
//    @RequestMapping(value = "/delete", method = RequestMethod.GET)
//    public ResponseModel delete(@RequestParam(name = "name") long name) {
//        return null;
//    }
}
