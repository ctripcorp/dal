package com.ctrip.framework.db.cluster.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Date;
import java.util.Map;

@Controller
public class WelcomeController {

    @Value("${application.message:Hello from db cluster!}")
    private String message = "Hello from db cluster!";

    @GetMapping("/")
    public String welcome(Map<String, Object> model) throws Exception {
        model.put("time", new Date());
        model.put("message", this.message);
        return "welcome";
    }

}