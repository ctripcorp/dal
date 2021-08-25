package com.ctrip.platform.dal.cluster.base;

/**
 * @author c7ch23en
 */
public interface Listenable<T> {

    void addListener(Listener<T> listener);

}
