package com.ctrip.framework.idgen.server.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DefaultHealthChecker {

//    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultHealthChecker.class);

    @RequestMapping(value = "/checkhealth")
    public String checkHealth() {
        return "OK";
    }

}
