package com.ctrip.framework.idgen.server.service;

import com.ctrip.framework.idgen.server.config.SnowflakeConfig;
import com.ctrip.framework.idgen.server.constant.CatConstants;
import com.ctrip.framework.idgen.server.exception.ServiceTimeoutException;
import com.ctrip.framework.idgen.server.exception.TimeRunOutException;
import com.ctrip.framework.idgen.service.api.IdSegment;
import com.dianping.cat.Cat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class CASSnowflakeWorker extends AbstractSnowflakeWorker {

    private static final Logger LOGGER = LoggerFactory.getLogger(CASSnowflakeWorker.class);

    protected final AtomicLong atomLastId = new AtomicLong(0);

    public CASSnowflakeWorker(String sequenceName, SnowflakeConfig config) {
        super(sequenceName, config);
    }

    @Override
    public List<IdSegment> generateIdPool(int requestSize, int timeoutMillis) {
        long startNanoTime = getNanoTime();
        requestSize = regulateRequestSize(requestSize);
        timeoutMillis = regulateTimeoutMillis(timeoutMillis);
        List<IdSegment> pool = new LinkedList<>();
        int remainedSize = requestSize;
        while (remainedSize > 0 && !isTimeout(startNanoTime, timeoutMillis)) {
            IdSegment segment = getSegment(remainedSize);
            if (segment != null) {
                pool.add(segment);
                remainedSize -= segment.getEnd().longValue() - segment.getStart().longValue() + 1;
            }
        }
        if (remainedSize > 0) {
            String msg = String.format("Generate id pool timeout within %d ms. " +
                    "Request size: %d, fetch size: %d", timeoutMillis,
                    requestSize, requestSize - remainedSize);
            if (pool.isEmpty()) {
                LOGGER.error(msg);
                throw new ServiceTimeoutException(msg);
            }
            LOGGER.warn(msg);
            Cat.logEvent(CatConstants.TYPE_ROOT, CatConstants.NAME_WORKER_TIMEOUT,
                    CatConstants.STATUS_WARN, msg);
        }
        return pool;
    }

    protected IdSegment getSegment(int requestSize) {
        long lastId = atomLastId.get();
        long lastTimestamp = parseTimestamp(lastId);
        long lastSequence = parseSequence(lastId);
        long startSequence;
        long endSequence;
        long timestamp = getTimestamp();

        if (timestamp > config.getMaxTimestamp()) {
            String msg = "Timestamp overflowed";
            LOGGER.error(msg);
            throw new TimeRunOutException(msg);
        }

        if (timestamp > lastTimestamp) {
            startSequence = getRandomSequence();
        } else {
            if (timestamp < lastTimestamp) {
                LOGGER.error(String.format("Clock moved backwards for %d ms", lastTimestamp - timestamp));
            }
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