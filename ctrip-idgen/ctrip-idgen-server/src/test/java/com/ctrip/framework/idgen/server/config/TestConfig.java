package com.ctrip.framework.idgen.server.config;

import com.ctrip.framework.foundation.Foundation;

import java.util.HashMap;
import java.util.Map;

public class TestConfig {

    public static ServerConfig mockServerConfig() {
        Map<String, String> map = new HashMap<>();
        String localhost = Foundation.net().getHostAddress();
        map.put("workerId_" + localhost, "1");
        ServerConfig config = new CtripServerConfig();
        config.importConfig(map);
        return config;
    }
}
