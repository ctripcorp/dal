package com.ctrip.framework.idgen.server.config;

import java.util.Map;

public interface ConfigChanged {

    void onConfigChanged(final Map<String, String> updatedConfig);

}
