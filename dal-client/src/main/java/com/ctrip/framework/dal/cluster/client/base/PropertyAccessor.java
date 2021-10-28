package com.ctrip.framework.dal.cluster.client.base;

import java.util.Set;

/**
 * @author c7ch23en
 */
public interface PropertyAccessor {

    void setProperty(String name, String value);

    boolean hasProperty(String name);

    String getProperty(String name);

    Set<String> getPropertyNames();

    void merge(PropertyAccessor parentProperties);

}
