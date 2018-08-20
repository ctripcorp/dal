package com.ctrip.platform.idgen.service;

import com.ctrip.platform.idgen.config.SnowflakeConfig;
import com.ctrip.platform.idgen.service.api.IdSegment;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class SnowflakeWorker extends SnowflakeConfig implements IdWorker {

    private String sequenceName;
    private long sequence;
    private long lastTimestamp = -1L;
    private boolean fallbackLocked = false;
    private Random rand = new Random();
    private SnowflakeConfig config;

    public SnowflakeWorker(String sequenceName) {
        this.sequenceName = sequenceName;
        sequence = getRandomSequence();
    }

    @Override
    public List<IdSegment> generateIdPool(long requestSize, int timeoutMillis) {

        if (requestSize <= 0) {
            return null;
        }

        long remainedSize = requestSize;
        timeoutMillis = timeoutMillis - 1;  // adjust timeout param
        long sequenceStart;
        long sequenceEnd;
        List<IdSegment> idSegments = new LinkedList<IdSegment>();

        synchronized (this) {

            long timestamp = getTimestamp();

            if (timestamp < lastTimestamp) {

                if (!fallbackLocked) {

                    sequenceStart = (sequence + 1) & sequenceMask;
                    if (sequenceStart == 0) {
                        fallbackLocked = true;
                        sequence = 0;
                        throw new RuntimeException("time fallback locked");
                    }

                    sequenceEnd = (remainedSize > (sequenceMask - sequenceStart + 1)) ?
                            sequenceMask : (sequenceStart + remainedSize - 1);
                    idSegments.add(constructIdSegment(lastTimestamp, sequenceStart, sequenceEnd));

                } else {

                    throw new RuntimeException("time fallback locked");

                }

                sequence = sequenceEnd;

            } else if (timestamp == lastTimestamp) {

                if (!fallbackLocked) {

                    sequenceStart = (sequence + 1) & sequenceMask;
                    if (sequenceStart == 0) {
                        sequenceStart = getRandomSequence();
                        timestamp = tilNextMillis(lastTimestamp);
                        timeoutMillis--;
                    }

                    while (remainedSize > (sequenceMask - sequenceStart + 1) && !isTimeout(timeoutMillis)) {
                        sequenceEnd = sequenceMask;
                        idSegments.add(constructIdSegment(timestamp, sequenceStart, sequenceEnd));
                        remainedSize = remainedSize - (sequenceEnd - sequenceStart + 1);
                        sequenceStart = getRandomSequence();
                        timestamp = tilNextMillis(timestamp);
                        timeoutMillis--;
                    }
                    sequenceEnd = (remainedSize > (sequenceMask - sequenceStart + 1)) ?
                            sequenceMask : (sequenceStart + remainedSize - 1);
                    idSegments.add(constructIdSegment(timestamp, sequenceStart, sequenceEnd));

                } else {

                    sequenceStart = getRandomSequence();
                    timestamp = tilNextMillis(lastTimestamp);
                    timeoutMillis--;

                    while (remainedSize > (sequenceMask - sequenceStart + 1) && !isTimeout(timeoutMillis)) {
                        sequenceEnd = sequenceMask;
                        idSegments.add(constructIdSegment(timestamp, sequenceStart, sequenceEnd));
                        remainedSize = remainedSize - (sequenceEnd - sequenceStart + 1);
                        sequenceStart = getRandomSequence();
                        timestamp = tilNextMillis(timestamp);
                        timeoutMillis--;
                    }
                    sequenceEnd = (remainedSize > (sequenceMask - sequenceStart + 1)) ?
                            sequenceMask : (sequenceStart + remainedSize - 1);
                    idSegments.add(constructIdSegment(timestamp, sequenceStart, sequenceEnd));

                }

                sequence = sequenceEnd;
                lastTimestamp = timestamp;
                fallbackLocked = false;

            } else {

                sequenceStart = getRandomSequence();

                while (remainedSize > (sequenceMask - sequenceStart + 1) && !isTimeout(timeoutMillis)) {
                    sequenceEnd = sequenceMask;
                    idSegments.add(constructIdSegment(timestamp, sequenceStart, sequenceEnd));
                    remainedSize = remainedSize - (sequenceEnd - sequenceStart + 1);
                    sequenceStart = getRandomSequence();
                    timestamp = tilNextMillis(timestamp);
                    timeoutMillis--;
                }
                sequenceEnd = (remainedSize > (sequenceMask - sequenceStart + 1)) ?
                        sequenceMask : (sequenceStart + remainedSize - 1);
                idSegments.add(constructIdSegment(timestamp, sequenceStart, sequenceEnd));

                sequence = sequenceEnd;
                lastTimestamp = timestamp;
                fallbackLocked = false;

            }

        }

        return idSegments;

    }

    private long getTimestamp() {
        return System.currentTimeMillis();
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

    private long getRandomSequence() {
        return getRandomSequence(sequenceInitRange);
    }

    private long getRandomSequence(int sequenceInitRange) {
        return rand.nextInt(sequenceInitRange);
    }

    private IdSegment constructIdSegment(long timestamp, long sequenceStart, long sequenceEnd) {
        return constructIdSegment(timestamp, workerId, sequenceStart, sequenceEnd);
    }

    private IdSegment constructIdSegment(long timestamp, long workerId, long sequenceStart, long sequenceEnd) {
        long idStart = constructId(timestamp, workerId, sequenceStart);
        long idEnd = constructId(timestamp, workerId, sequenceEnd);
        return new IdSegment(idStart, idEnd);
    }

    private long constructId(long timestamp, long sequence) {
        return constructId(timestamp, workerId, sequence);
    }

    private long constructId(long timestamp, long workerId, long sequence) {
        return (((timestamp - timestampReference) & timestampMask) << timestampShift) |
                (workerId << workerIdShift) | (sequence);
    }

}
