package com.ctrip.framework.idgen.server.config;

import com.ctrip.framework.idgen.server.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CtripWhitelist implements Whitelist, ConfigConstants {

    private static final Logger LOGGER = LoggerFactory.getLogger(CtripWhitelist.class);

    private Set<String> whitelist = new HashSet<>();

    public void importConfig(Map<String, String> properties) {
        if (null == properties) {
            return;
        }
        whitelist = new HashSet<>();
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null &&
                    WHITELIST_ENABLED_FLAG.equalsIgnoreCase(entry.getValue().trim())) {
                whitelist.add(entry.getKey());
            }
        }
        String initialList = StringUtils.setToString(whitelist, ", ");
        LOGGER.info("Initial whitelist: " + initialList);
    }

    public boolean validateSequenceName(String sequenceName) {
        return (sequenceName != null && whitelist != null && whitelist.contains(sequenceName));
    }

    public void refreshConfig(Map<String, String> properties) {
        if (null == properties) {
            return;
        }

        Set<String> updatedWhitelist = new HashSet<>();
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null &&
                    WHITELIST_ENABLED_FLAG.equalsIgnoreCase(entry.getValue().trim())) {
                updatedWhitelist.add(entry.getKey());
            }
        }

        Set<String> tempSet = new HashSet<>(whitelist);
        tempSet.removeAll(updatedWhitelist);
        String removedList = StringUtils.setToString(tempSet, ", ");
        if (removedList != null) {
            LOGGER.info("Removed from whitelist: " + removedList);
        }
        tempSet.clear();
        tempSet.addAll(updatedWhitelist);
        tempSet.removeAll(whitelist);
        String addedList = StringUtils.setToString(tempSet, ", ");
        if (addedList != null) {
            LOGGER.info("Added to whitelist: " + addedList);
        }

        whitelist = updatedWhitelist;
    }

}
