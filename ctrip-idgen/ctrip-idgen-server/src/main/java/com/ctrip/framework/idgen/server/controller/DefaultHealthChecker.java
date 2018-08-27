package com.ctrip.framework.idgen.server.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DefaultHealthChecker {

    @RequestMapping(value = "/checkhealth")
    public String checkHealth() {
        return "OK";
    }

}
