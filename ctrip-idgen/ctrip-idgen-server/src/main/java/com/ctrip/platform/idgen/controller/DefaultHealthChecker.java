package com.ctrip.platform.idgen.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DefaultHealthChecker {

    @RequestMapping(value = "/healthCheck")
    public String healthCheck() {
        System.out.println("=== here i come ===");
        return "OK";
    }

}
