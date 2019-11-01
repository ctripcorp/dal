package com.ctrip.framework.db.cluster.controller;

import com.ctrip.framework.db.cluster.vo.ResponseModel;
import com.ctrip.framework.db.cluster.vo.dal.create.InstanceVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by shenjie on 2019/3/14.
 */
@Slf4j
@RestController
@RequestMapping("/instance")
public class InstanceController {
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseModel add(@RequestBody InstanceVo instanceVo, HttpServletRequest request) {
        return null;
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResponseModel update() {
        return null;
    }

    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public ResponseModel query(@RequestParam(name = "name") long name) {
        return null;
    }

    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public ResponseModel delete(@RequestParam(name = "name") long name) {
        return null;
    }
}