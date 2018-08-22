package com.ctrip.framework.idgen.server.service;

import com.ctrip.framework.idgen.server.config.ConfigConstants;
import com.ctrip.framework.idgen.server.config.ServerConfig;
import com.ctrip.platform.idgen.service.api.IdSegment;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class SnowflakeWorker implements IdWorker {

    private volatile String sequenceName;
    private volatile long sequence;
    private volatile long lastTimestamp = -1L;
    private volatile boolean fallbackLocked = false;
    private Random rand = new Random();
    private ServerConfig config;

    public SnowflakeWorker(String sequenceName, ServerConfig config) {
        this.sequenceName = sequenceName;
        this.config = config;
        this.sequence = getRandomSequence();
    }

    @Override
    public List<IdSegment> generateIdPool(int requestSize, int timeoutMillis) {

        long startNanoTime = getNanoTime();

        if (requestSize <= 0) {
            return null;
        }
        if (requestSize > ConfigConstants.REQUESTSIZE_MAX_VALUE) {
            requestSize = ConfigConstants.REQUESTSIZE_MAX_VALUE;
        }
        if (timeoutMillis <= 0) {
            timeoutMillis = ConfigConstants.TIMEOUTMILLIS_DEFAULT_VALUE;
        }
        if (timeoutMillis > ConfigConstants.TIMEOUTMILLIS_MAX_VALUE) {
            timeoutMillis = ConfigConstants.TIMEOUTMILLIS_MAX_VALUE;
        }

        long sequenceStart;

        synchronized (this) {

            long timestamp = getTimestamp();

            if (timestamp < lastTimestamp) {

                if (!fallbackLocked) {

                    sequenceStart = (sequence + 1) & config.getSequenceMask();
                    if (sequenceStart == 0) {
                        fallbackLocked = true;
                        throw new RuntimeException("Time fallback locked");
                    }

                } else {

                    throw new RuntimeException("Time fallback locked");

                }

                return generateIdSegments(sequenceStart, lastTimestamp, requestSize,
                        startNanoTime, timeoutMillis, false);

            } else if (timestamp == lastTimestamp) {

                if (!fallbackLocked) {

                    sequenceStart = (sequence + 1) & config.getSequenceMask();
                    if (sequenceStart == 0) {
                        sequenceStart = getRandomSequence();
                        timestamp = tilNextMillis(lastTimestamp);
                    }

                } else {

                    sequenceStart = getRandomSequence();
                    timestamp = tilNextMillis(lastTimestamp);

                }

                return generateIdSegments(sequenceStart, timestamp, requestSize,
                        startNanoTime, timeoutMillis, true);

            } else {

                sequenceStart = getRandomSequence();

                return generateIdSegments(sequenceStart, timestamp, requestSize,
                        startNanoTime, timeoutMillis, true);

            }

        }

    }

    private List<IdSegment> generateIdSegments(long sequenceStart, long timestamp, int requestSize,
                                               long startNanoTime, int timeoutMillis, boolean loopFlag) {
        List<IdSegment> idSegments = new LinkedList<>();
        long remainedSize = requestSize;
        long sequenceEnd;
        long sequenceMask = config.getSequenceMask();

        while (loopFlag && !isTimeout(startNanoTime, timeoutMillis) &&
                remainedSize > (sequenceMask - sequenceStart + 1)) {
            sequenceEnd = sequenceMask;
            idSegments.add(constructIdSegment(timestamp, sequenceStart, sequenceEnd));
            remainedSize = remainedSize - (sequenceEnd - sequenceStart + 1);
            sequenceStart = getRandomSequence();
            timestamp = tilNextMillis(timestamp);
        }

        sequenceEnd = (remainedSize > (sequenceMask - sequenceStart + 1)) ?
                sequenceMask : (sequenceStart + remainedSize - 1);
        idSegments.add(constructIdSegment(timestamp, sequenceStart, sequenceEnd));

        if (loopFlag) {
            lastTimestamp = timestamp;
        }
        sequence = sequenceEnd;
        fallbackLocked = false;

        return idSegments;
    }

    private long getTimestamp() {
        return System.currentTimeMillis();
    }

    private long getNanoTime() {
        return System.nanoTime();
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = getTimestamp();
        while (timestamp <= lastTimestamp) {
            timestamp = getTimestamp();
        }
        return timestamp;
    }

    private boolean isTimeout(int timeoutMillis) {
        return (timeoutMillis == 0);
    }

    private boolean isTimeout(long startNanoTime, int timeoutMillis) {
        return ((getNanoTime() - startNanoTime) >= (timeoutMillis * 10 ^ 6));
    }

    private long getRandomSequence() {
        return getRandomSequence(config.getSequenceInitRange());
    }

    private long getRandomSequence(int sequenceInitRange) {
        return rand.nextInt(sequenceInitRange);
    }

    private IdSegment constructIdSegment(long timestamp, long sequenceStart, long sequenceEnd) {
        return constructIdSegment(timestamp, config.getWorkerId(), sequenceStart, sequenceEnd);
    }

    private IdSegment constructIdSegment(long timestamp, long workerId, long sequenceStart, long sequenceEnd) {
        long idStart = constructId(timestamp, workerId, sequenceStart);
        long idEnd = constructId(timestamp, workerId, sequenceEnd);
        return new IdSegment(idStart, idEnd);
    }

    private long constructId(long timestamp, long sequence) {
        return constructId(timestamp, config.getWorkerId(), sequence);
    }

    private long constructId(long timestamp, long workerId, long sequence) {
        return (((timestamp - config.getTimestampReference()) & config.getTimestampMask()) << config.getTimestampShift()) |
                (workerId << config.getWorkerIdShift()) | (sequence);
    }

}
