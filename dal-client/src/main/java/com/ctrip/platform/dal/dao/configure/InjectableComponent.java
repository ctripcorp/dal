package com.ctrip.platform.dal.dao.configure;

/**
 * @author c7ch23en
 */
public interface InjectableComponent<T> extends DalComponent {

    void inject(T object);

}
