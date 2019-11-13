package com.ctrip.framework.db.cluster;

import com.ctrip.framework.db.cluster.utils.EnvUtil;
import org.junit.Test;
import org.springframework.boot.SpringApplication;

import java.awt.*;
import java.net.URI;

public class WebStarter {

    public static void main(String[] args) throws Exception {
        System.setProperty("java.awt.headless", "false");

        SpringApplication.run(WebInitializer.class);

        // port 8080 is configured in src/test/resources/application.properties(key: server.port)
        Desktop.getDesktop().browse(new URI("http://127.0.0.1:8080"));
    }

    @Test
    public void testLocalEnv() {
        System.out.println(EnvUtil.getEnv());
    }
}
