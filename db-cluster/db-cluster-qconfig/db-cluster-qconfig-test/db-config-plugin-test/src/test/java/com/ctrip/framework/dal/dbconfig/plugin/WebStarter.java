package com.ctrip.framework.dal.dbconfig.plugin;

import java.awt.Desktop;
import java.net.URI;

import org.springframework.boot.SpringApplication;

public class WebStarter {

  public static void main(String[] args) throws Exception {
    System.setProperty("java.awt.headless", "false");

    SpringApplication.run(WebInitializer.class);

    // port 8080 is configured in src/test/resources/application.properties(key: server.port)
    Desktop.getDesktop().browse(new URI("http://127.0.0.1:8080"));
  }
}
