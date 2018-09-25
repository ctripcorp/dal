package com.ctrip.framework.idgen.server.service;

import com.ctrip.framework.idgen.server.config.SnowflakeConfig;
import com.ctrip.platform.idgen.service.api.IdSegment;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class CASSnowflakeWorker extends AbstractSnowflakeWorker {

    private final AtomicLong atomLastId = new AtomicLong(0);

    public CASSnowflakeWorker(String sequenceName, SnowflakeConfig config) {
        super(sequenceName, config);
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

        if (timestamp > config.getMaxTimestamp()) {
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

}
