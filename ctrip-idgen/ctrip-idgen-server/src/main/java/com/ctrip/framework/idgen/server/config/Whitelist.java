package com.ctrip.framework.idgen.server.config;

public interface Whitelist<T> {

    void load(T config);

    boolean validate(String name);

}
