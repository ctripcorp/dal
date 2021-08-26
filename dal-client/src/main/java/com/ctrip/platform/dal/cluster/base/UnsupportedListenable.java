package com.ctrip.platform.dal.cluster.base;

/**
 * @author c7ch23en
 */
public abstract class UnsupportedListenable<T> implements Listenable<T> {

    @Override
    public void addListener(Listener<T> listener) {}

}
