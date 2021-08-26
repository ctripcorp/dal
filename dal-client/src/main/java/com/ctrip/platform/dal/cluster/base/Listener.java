package com.ctrip.platform.dal.cluster.base;

/**
 * @author c7ch23en
 */
public interface Listener<T> {

    void onChanged(T current);

}
