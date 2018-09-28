package com.ctrip.framework.idgen.server.config;

public interface SnowflakeConfig<T> {

    void load(T config);

    boolean differs(SnowflakeConfig another);

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
