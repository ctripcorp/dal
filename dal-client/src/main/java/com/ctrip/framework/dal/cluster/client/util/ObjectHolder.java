package com.ctrip.framework.dal.cluster.client.util;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author c7ch23en
 */
public class ObjectHolder<T> {

    private final AtomicReference<T> objectRef;

    public ObjectHolder() {
        objectRef = new AtomicReference<>();
    }

    public ObjectHolder(T initialObject) {
        objectRef = new AtomicReference<>(initialObject);
    }

    public T getOrCreate(Creator<T> creator) {
        T object = objectRef.get();
        if (object == null)
            synchronized (objectRef) {
                object = objectRef.get();
                if (object == null && creator != null) {
                    object = creator.create();
                    objectRef.set(object);
                }
            }
        return object;
    }

    public T getAndSet(T newObject) {
        return objectRef.getAndSet(newObject);
    }

    public T get() {
        return objectRef.get();
    }

    public void set(T newObject) {
        objectRef.set(newObject);
    }

    public interface Creator<T> {
        T create();
    }

}
