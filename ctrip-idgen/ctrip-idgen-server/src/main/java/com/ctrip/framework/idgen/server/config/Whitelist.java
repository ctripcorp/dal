package com.ctrip.framework.idgen.server.config;

import java.util.Map;

public interface Whitelist {

    void importConfig(Map<String, String> properties);

    boolean validateSequenceName(String sequenceName);

    void refreshConfig(Map<String, String> properties);

}
