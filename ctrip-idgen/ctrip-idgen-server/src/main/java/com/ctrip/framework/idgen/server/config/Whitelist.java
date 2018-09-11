package com.ctrip.framework.idgen.server.config;

import java.util.Map;

public interface Whitelist {

    void load(Map<String, String> config);

    boolean validate(String name);

}
