package com.ctrip.framework.idgen.server.service;

import com.ctrip.framework.idgen.server.config.SnowflakeConfig;
import com.ctrip.framework.idgen.service.api.IdSegment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

public class SnowflakeWorker extends AbstractSnowflakeWorker {

    private static final Logger LOGGER = LoggerFactory.getLogger(SnowflakeWorker.class);

    private volatile long sequence;
    private volatile long lastTimestamp = -1L;
    private volatile boolean fallbackLocked = false;

    public SnowflakeWorker(String sequenceName, SnowflakeConfig config) {
        super(sequenceName, config);
        sequence = getRandomSequence();
    }

    @Override
    public List<IdSegment> generateIdPool(int requestSize, int timeoutMillis) {

        long startNanoTime = getNanoTime();

        if (requestSize <= 0) {
            return null;
        }
        if (requestSize > REQUESTSIZE_MAX_VALUE) {
            requestSize = REQUESTSIZE_MAX_VALUE;
        }
        if (timeoutMillis <= 0) {
            timeoutMillis = TIMEOUTMILLIS_DEFAULT_VALUE;
        }
        if (timeoutMillis > TIMEOUTMILLIS_MAX_VALUE) {
            timeoutMillis = TIMEOUTMILLIS_MAX_VALUE;
        }

        long sequenceStart;

        synchronized (this) {

            long timestamp = getTimestamp();

            if (timestamp < lastTimestamp) {

                if (!fallbackLocked) {

                    sequenceStart = (sequence + 1) & config.getSequenceMask();
                    if (sequenceStart == 0) {
                        fallbackLocked = true;
                        LOGGER.error("Time fallback locked. Timestamp: " + timestamp + ", Last timestamp: " + lastTimestamp);
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

//        LOGGER.debug("fetId success, sequenceEnd: " + sequenceEnd);

        return idSegments;
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = getTimestamp();
        while (timestamp <= lastTimestamp) {
            timestamp = getTimestamp();
        }
        return timestamp;
    }

    private IdSegment constructIdSegment(long timestamp, long sequenceStart, long sequenceEnd) {
        return constructIdSegment(timestamp, config.getWorkerId(), sequenceStart, sequenceEnd);
    }

    private IdSegment constructIdSegment(long timestamp, long workerId, long sequenceStart, long sequenceEnd) {
        long idStart = constructId(timestamp, workerId, sequenceStart);
        long idEnd = constructId(timestamp, workerId, sequenceEnd);
        return new IdSegment(idStart, idEnd);
    }

    // For unit test
    protected void setLastTimestamp(long lastTimestamp) {
        this.lastTimestamp = lastTimestamp;
    }

    // For unit test
    protected void setFallbackLocked(boolean fallbackLocked) {
        this.fallbackLocked = fallbackLocked;
    }

    // For unit test
    protected void setSequence(long sequence) {
        this.sequence = sequence;
    }

}
