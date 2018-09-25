package com.ctrip.framework.idgen.server.config;

import java.util.Map;

public interface SnowflakeConfig{

    void load(Map<String, String> properties);

    long getWorkerId();

    int getTimestampBits();

    int getWorkerIdBits();

    int getSequenceBits();

    long getIdReference();

    String getDateReference();

    int getSequenceResetRange();

    int getTimestampShift();

    int getWorkerIdShift();

    long getMaxTimestamp();

    long getSequenceMask();

    long getTimestampReference();

}
