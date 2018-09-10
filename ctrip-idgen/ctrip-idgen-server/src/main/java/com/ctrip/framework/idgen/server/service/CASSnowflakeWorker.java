package com.ctrip.framework.idgen.server.service;

import com.ctrip.framework.idgen.server.config.ServerConfig;
import com.ctrip.platform.idgen.service.api.IdSegment;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class CASSnowflakeWorker implements IdWorker {

    private volatile String sequenceName;
    private final AtomicLong atomLastId = new AtomicLong(0);
    private Random rand = new Random();
    private ServerConfig config;

    public CASSnowflakeWorker(String sequenceName, ServerConfig config) {
        this.sequenceName = sequenceName;
        this.config = config;
    }

    @Override
    public List<IdSegment> generateIdPool(int requestSize, int timeoutMillis) {
        long startNanoTime = getNanoTime();
        List<IdSegment> pool = new LinkedList<>();
        while (requestSize > 0 && !isTimeout(startNanoTime, timeoutMillis)) {
            IdSegment segment = getSegment(requestSize);
            if (segment != null) {
                pool.add(segment);
                requestSize -= segment.getEnd().longValue() - segment.getStart().longValue() + 1;
            }
        }
        return pool;
    }

    private IdSegment getSegment(int requestSize) {
        long lastId = atomLastId.get();
        long lastTimestamp = parseTimestamp(lastId);
        long lastSequence = parseSequence(lastId);
        long startSequence;
        long endSequence;
        long timestamp = getTimestamp();

        if (timestamp > config.getTimestampMask()) {
            return null;
        }

        if (timestamp > lastTimestamp) {
            startSequence = getRandomSequence();
        } else {
            startSequence = (lastSequence + 1) & config.getSequenceMask();
            if (startSequence == 0) {
                return null;
            }
            timestamp = lastTimestamp;
        }

        long remainedSize = config.getSequenceMask() - startSequence + 1;
        if (remainedSize > requestSize) {
            endSequence = startSequence + requestSize - 1;
        } else {
            endSequence = config.getSequenceMask();
        }

        long endId = constructId(timestamp, endSequence);
        if (atomLastId.compareAndSet(lastId, endId)) {
            long startId = constructId(timestamp, startSequence);
            return new IdSegment(startId, endId);
        } else {
            return null;
        }
    }

    private long getMilliTime() {
        return System.currentTimeMillis();
    }

    private long getNanoTime() {
        return System.nanoTime();
    }

    private long getTimestamp() {
        return getMilliTime() - config.getTimestampReference();
    }

    private boolean isTimeout(long startNanoTime, int timeoutMillis) {
        return (getNanoTime() - startNanoTime) >= (timeoutMillis * 1000000L);
    }

    private long constructId(long timestamp, long sequence) {
        return constructId(timestamp, config.getWorkerId(), sequence);
    }

    private long constructId(long timestamp, long workerId, long sequence) {
        return (timestamp << config.getTimestampShift()) | (workerId << config.getWorkerIdShift()) | (sequence);
    }

    private long parseTimestamp(long id) {
        return id >> config.getTimestampShift();
    }

    private long parseSequence(long id) {
        return id & config.getSequenceMask();
    }

    private long getRandomSequence() {
        return getRandomSequence(config.getSequenceInitRange());
    }

    private long getRandomSequence(int sequenceInitRange) {
        return rand.nextInt(sequenceInitRange);
    }

}
