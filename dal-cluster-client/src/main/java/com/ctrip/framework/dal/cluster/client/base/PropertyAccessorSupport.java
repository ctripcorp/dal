package com.ctrip.framework.dal.cluster.client.base;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author c7ch23en
 */
public abstract class PropertyAccessorSupport implements PropertyAccessor {

    private final Map<String, String> properties = new LinkedHashMap<>();

    public PropertyAccessorSupport() {}

    @Override
    public void setProperty(String name, String value) {
        properties.put(name, value);
    }

    @Override
    public boolean hasProperty(String name) {
        return properties.containsKey(name);
    }

    @Override
    public String getProperty(String name) {
        return properties.get(name);
    }

    @Override
    public Set<String> getPropertyNames() {
        return properties.keySet();
    }

    @Override
    public void merge(PropertyAccessor parentProperties) {
        if (parentProperties == null)
            return;
        for (String parentPropertyName : parentProperties.getPropertyNames()) {
            if (!hasProperty(parentPropertyName))
                setProperty(parentPropertyName, parentProperties.getProperty(parentPropertyName));
        }
    }

}
