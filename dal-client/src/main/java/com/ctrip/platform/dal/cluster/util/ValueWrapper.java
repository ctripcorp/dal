package com.ctrip.platform.dal.cluster.util;

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
