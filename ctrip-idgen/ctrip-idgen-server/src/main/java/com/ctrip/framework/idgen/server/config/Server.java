package com.ctrip.framework.idgen.server.config;

public interface Server<T> {

    void initialize(T config);

    long getWorkerId();

}
