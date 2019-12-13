package com.ctrip.framework.dal.cluster.client.base;

import com.ctrip.framework.dal.cluster.client.cluster.ClusterSwitchedEvent;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author c7ch23en
 */
public abstract class ListenableSupport<T> implements Listenable<T> {

    private Set<Listener<T>> listeners = new LinkedHashSet<>();

    @Override
    public synchronized void addListener(Listener<T> listener) {
        listeners.add(listener);
    }

    protected synchronized Set<Listener<T>> getListeners() {
        return new LinkedHashSet<>(listeners);
    }

    protected void executeListeners(T object) {
        for (Listener<T> listener : getListeners()) {
            try {
                listener.onChanged(object);
            } catch (Throwable t) {
                // ignore
            }
        }
    }

}
