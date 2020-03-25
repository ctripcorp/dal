package com.ctrip.platform.dal.dao;

/**
 * @author c7ch23en
 */
public interface DalCallback<T extends CallbackContext> {

    void process(T context);

}
