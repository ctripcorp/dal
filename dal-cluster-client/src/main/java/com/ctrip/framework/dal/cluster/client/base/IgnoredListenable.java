package com.ctrip.framework.dal.cluster.client.base;

/**
 * @author c7ch23en
 */
public abstract class IgnoredListenable<T> implements Listenable<T> {

    @Override
    public void addListener(Listener<T> listener) {}

}
