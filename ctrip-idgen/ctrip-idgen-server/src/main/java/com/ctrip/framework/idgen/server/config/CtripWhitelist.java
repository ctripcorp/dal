package com.ctrip.framework.idgen.server.config;

import com.ctrip.framework.idgen.server.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CtripWhitelist implements Whitelist {

    private static final Logger LOGGER = LoggerFactory.getLogger(CtripWhitelist.class);
    private static final String WHITELIST_ENABLED_FLAG = "on";
    private Set<String> whitelist = new HashSet<>();

    public void load(Map<String, String> config) {
        if (null == config) {
            return;
        }

        Set<String> enabledList = new HashSet<>();
        for (Map.Entry<String, String> entry : config.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key != null && value != null && WHITELIST_ENABLED_FLAG.equalsIgnoreCase(value.trim())) {
                enabledList.add(key.trim());
            }
        }

        // Parse removed list
        Set<String> tempSet = new HashSet<>(whitelist);
        tempSet.removeAll(enabledList);
        String removedList = StringUtils.setToString(tempSet, ", ");
        if (!StringUtils.isEmpty(removedList)) {
            LOGGER.info("Removed from whitelist: {}", removedList);
        }

        // Parse added list
        tempSet.clear();
        tempSet.addAll(enabledList);
        tempSet.removeAll(whitelist);
        String addedList = StringUtils.setToString(tempSet, ", ");
        if (!StringUtils.isEmpty(addedList)) {
            LOGGER.info("Added to whitelist: {}", addedList);
        }

        whitelist = enabledList;
    }

    public boolean validate(String name) {
        return (name != null && whitelist.contains(name.trim()));
    }

}
