package com.ctrip.framework.dal.cluster.client.base;

/**
 * @author c7ch23en
 */
public interface Listener<T> {

    void onChanged(T current);

}
