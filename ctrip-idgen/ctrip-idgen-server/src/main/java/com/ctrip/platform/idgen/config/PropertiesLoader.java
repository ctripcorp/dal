package com.ctrip.platform.idgen.config;

import qunar.tc.qconfig.client.MapConfig;

import java.util.Map;

public class PropertiesLoader {

    private static final String SERVER_PROPERTIES = "server.properties";
    private static final String REGISTER_PROPERTIES = "register.properties";

    private static Map<String, String> serverProperties;
    private static Map<String, String> registerProperties;

    public static void initialize() {
        MapConfig mapConfig = MapConfig.get(SERVER_PROPERTIES);
        serverProperties = mapConfig.asMap();
        mapConfig = MapConfig.get(REGISTER_PROPERTIES);
        registerProperties = mapConfig.asMap();
    }

    public static Map<String, String> getServerProperties() {
        return serverProperties;
    }

    public static Map<String, String> getRegisterProperties() {
        return registerProperties;
    }

}
