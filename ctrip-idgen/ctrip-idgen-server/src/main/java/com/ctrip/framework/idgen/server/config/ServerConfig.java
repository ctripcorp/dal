package com.ctrip.framework.idgen.server.config;

import java.util.Map;

public interface ServerConfig {

    void importConfig(Map<String, String> properties);

    long getWorkerId();

    int getTimestampShift();

    int getWorkerIdShift();

    long getTimestampMask();

    long getSequenceMask();

    int getSequenceInitRange();

    long getTimestampReference();

}
