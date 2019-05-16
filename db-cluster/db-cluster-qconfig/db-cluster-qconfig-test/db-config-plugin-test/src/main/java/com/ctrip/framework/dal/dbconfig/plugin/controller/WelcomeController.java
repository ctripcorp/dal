package com.ctrip.framework.dal.dbconfig.plugin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class WelcomeController {

    private static final Logger log = LoggerFactory.getLogger(WelcomeController.class);

    @Value("${application.message:Hello from Ctrip Framework!}")
    private String message = "Hello from Ctrip Framework!";

    @GetMapping("/")
    public String welcome(Map<String, Object> model) throws Exception {
        model.put("message", this.message);
        return "welcome";
    }

}