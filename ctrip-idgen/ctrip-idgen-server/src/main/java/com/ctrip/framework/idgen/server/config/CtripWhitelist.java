package com.ctrip.framework.idgen.server.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

public class CtripWhitelist implements Whitelist, ConfigConstants {

    private static final Logger LOGGER = LoggerFactory.getLogger(CtripServerConfig.class);

    private Set<String> whitelist;

    public void importConfig(Map<String, String> properties) {
        if (null == properties) {
            return;
        }

        for (Map.Entry<String, String> entry : properties.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null &&
                    WHITELIST_ENABLE_FLAG.equalsIgnoreCase(entry.getValue().trim())) {
                whitelist.add(entry.getKey());
            }
        }
    }

    public boolean validateSequenceName(String sequenceName) {
        return (sequenceName != null && whitelist != null && whitelist.contains(sequenceName));
    }

}
