package com.ctrip.framework.idgen.server.config;

import com.ctrip.framework.idgen.server.constant.CatConstants;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class CtripWhitelist implements Whitelist<Map<String, String>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CtripWhitelist.class);

    private static final String WHITELIST_ENABLED_FLAG = "on";
    private static final String CAT_NAME_WHITELIST_CHANGED = "Whitelist.changed";

    private final AtomicReference<Set<String>> whitelist = new AtomicReference<>();

    public CtripWhitelist() {
        whitelist.set(new HashSet<String>());
    }

    public void load(Map<String, String> properties) {
        if (null == properties) {
            return;
        }

        Set<String> updated = new HashSet<>();
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key != null && value != null && WHITELIST_ENABLED_FLAG.equalsIgnoreCase(value.trim())) {
                updated.add(key.trim());
            }
        }

        Set<String> previous = whitelist.getAndSet(updated);

        compare(previous, updated);
    }

    public boolean validate(String name) {
        if (null == name) {
            return false;
        }
        return whitelist.get().contains(name.trim());
    }

    private void compare(final Set<String> previous, final Set<String> updated) {
        // Parse removed list
        Set<String> temp = new HashSet<>(previous);
        temp.removeAll(updated);
        if (!temp.isEmpty()) {
            String removedList = setToString(temp, ", ");
            LOGGER.info("Removed from whitelist: {}", removedList);
            Cat.logEvent(CatConstants.CAT_TYPE_IDGEN_SERVER, CAT_NAME_WHITELIST_CHANGED,
                    Event.SUCCESS, removedList);
        }
        // Parse added list
        temp.clear();
        temp.addAll(updated);
        temp.removeAll(previous);
        if (!temp.isEmpty()) {
            String addedList = setToString(temp, ", ");
            LOGGER.info("Added to whitelist: {}", addedList);
            Cat.logEvent(CatConstants.CAT_TYPE_IDGEN_SERVER, CAT_NAME_WHITELIST_CHANGED,
                    Event.SUCCESS, addedList);
        }
    }

    private String setToString(final Set<String> set, String separator) {
        if (null == set || set.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        Iterator<String> iterator = set.iterator();
        while (iterator.hasNext()) {
            sb.append(iterator.next());
            if (iterator.hasNext()) {
                sb.append(separator);
            }
        }
        return sb.toString();
    }

}
