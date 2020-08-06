package com.ctrip.platform.dal.dao.configure;

/**
 * @author c7ch23en
 */
public interface ResourceLoader<T> {

    Resource<T> getResource(String resourceName);

}
