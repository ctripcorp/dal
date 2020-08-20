package com.ctrip.platform.dal.dao.configure;

/**
 * @author c7ch23en
 */
public interface Resource<T> {

    T getContent();

    boolean isEmpty();

}
