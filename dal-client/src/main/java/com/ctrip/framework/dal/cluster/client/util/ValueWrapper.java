package com.ctrip.framework.dal.cluster.client.util;

/**
 * @author c7ch23en
 */
public class ValueWrapper<V> {

    private final V value;

    public ValueWrapper(V value) {
        this.value = value;
    }

    public V getValue() {
        return value;
    }

}
