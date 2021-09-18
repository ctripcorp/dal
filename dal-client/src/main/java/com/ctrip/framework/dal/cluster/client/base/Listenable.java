package com.ctrip.framework.dal.cluster.client.base;

/**
 * @author c7ch23en
 */
public interface Listenable<T> {

    void addListener(Listener<T> listener);

}
