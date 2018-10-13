package com.ctrip.framework.idgen.server.service;

import com.ctrip.framework.idgen.server.config.SnowflakeConfig;

import java.util.Random;

public abstract class AbstractSnowflakeWorker implements IdWorker {

    protected static final int REQUESTSIZE_MAX_VALUE = 5000;
    protected static final int TIMEOUTMILLIS_MAX_VALUE = 800;
    protected static final int TIMEOUTMILLIS_DEFAULT_VALUE = 200;

    protected final String sequenceName;
    protected final SnowflakeConfig config;
    protected Random random = new Random();

    public AbstractSnowflakeWorker(String sequenceName, SnowflakeConfig config) {
        this.sequenceName = sequenceName;
        this.config = config;
    }

    protected long getMilliTime() {
        return System.currentTimeMillis();
    }

    protected long getNanoTime() {
        return System.nanoTime();
    }

    protected long getTimestamp() {
        return getMilliTime() - config.getTimestampReference();
    }

    protected boolean isTimeout(long startNanoTime, int timeoutMillis) {
        return (getNanoTime() - startNanoTime) >= (timeoutMillis * 1000000L);
    }

    protected long constructId(long timestamp, long sequence) {
        return constructId(timestamp, config.getWorkerId(), sequence);
    }

    protected long constructId(long timestamp, long workerId, long sequence) {
        return (timestamp << config.getTimestampShift()) | (workerId << config.getWorkerIdShift()) | (sequence);
    }

    protected long parseTimestamp(long id) {
        return id >> config.getTimestampShift();
    }

    protected long parseSequence(long id) {
        return id & config.getSequenceMask();
    }

    protected long getRandomSequence() {
        return getRandomSequence(config.getSequenceResetRange());
    }

    protected long getRandomSequence(int sequenceInitRange) {
        return random.nextInt(sequenceInitRange);
    }

}
