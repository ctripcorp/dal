package com.ctrip.framework.idgen.server.config;

public interface ConfigChangedListener<T> {

    void onConfigChanged(final T updatedConfig);

}
