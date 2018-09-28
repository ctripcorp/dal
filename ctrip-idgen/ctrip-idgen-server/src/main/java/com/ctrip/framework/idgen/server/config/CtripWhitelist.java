package com.ctrip.framework.idgen.server.config;

import com.ctrip.framework.idgen.server.constant.CatConstants;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class CtripWhitelist implements Whitelist<Map<String, String>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CtripWhitelist.class);

    private static final String WHITELIST_ENABLED_FLAG = "on";

    private AtomicReference<Set<String>> whitelist = new AtomicReference<>();

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
                updated.add(key.trim().toLowerCase());
            }
        }
        Set<String> previous = whitelist.getAndSet(updated);
        compare(previous, updated);
    }

    public boolean validate(String name) {
        return whitelist.get().contains(name.trim().toLowerCase());
    }

    private void compare(final Set<String> previous, final Set<String> updated) {
        // Added list
        Set<String> temp = new HashSet<>(updated);
        temp.removeAll(previous);
        if (!temp.isEmpty()) {
            String addedList = join(temp, ", ");
            String msg = String.format("Added whitelist: %s", addedList);
            LOGGER.info(msg);
            Cat.logEvent(CatConstants.CAT_TYPE_IDGEN_SERVER, CatConstants.CAT_NAME_WHITELIST_CHANGED,
                    Event.SUCCESS, msg);
        }
        // Removed list
        temp.clear();
        temp.addAll(previous);
        temp.removeAll(updated);
        if (!temp.isEmpty()) {
            String removedList = join(temp, ", ");
            String msg = String.format("Removed whitelist: %s", removedList);
            LOGGER.info(msg);
            Cat.logEvent(CatConstants.CAT_TYPE_IDGEN_SERVER, CatConstants.CAT_NAME_WHITELIST_CHANGED,
                    Event.SUCCESS, msg);
        }
    }

    private String join(final Collection<String> collection, String separator) {
        if (null == collection) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        Iterator<String> iterator = collection.iterator();
        while (iterator.hasNext()) {
            builder.append(iterator.next());
            if (iterator.hasNext()) {
                builder.append(separator);
            }
        }
        return builder.toString();
    }

}
