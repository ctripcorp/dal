package com.ctrip.framework.idgen.client;

import com.ctrip.framework.idgen.client.strategy.DefaultStrategy;
import com.ctrip.framework.idgen.client.strategy.PrefetchStrategy;
import com.ctrip.platform.dal.sharding.idgen.IdGenerator;
import com.ctrip.platform.idgen.service.api.IdGenRequestType;
import com.ctrip.platform.idgen.service.api.IdGenResponseType;
import com.ctrip.platform.idgen.service.api.IdGenerateService;
import com.ctrip.platform.idgen.service.api.IdSegment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public class StaticIdGenerator implements IdGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(StaticIdGenerator.class);

    private String sequenceName;
    private Deque<IdSegment> idSegments = new ConcurrentLinkedDeque<>();
    private long initialSize = 0;
    private long currentId = -1;
    private long remainedSize = 0;
    private final PrefetchStrategy strategy;

    public StaticIdGenerator(String sequenceName) {
        this(sequenceName, new DefaultStrategy());
    }

    public StaticIdGenerator(String sequenceName, final PrefetchStrategy strategy) {
        this.sequenceName = sequenceName;
        this.strategy = (strategy != null) ? strategy : new DefaultStrategy();
        initialize();
    }

    private void initialize() {
        idSegments = new ConcurrentLinkedDeque<>();
        initialSize = 0;
        remainedSize = 0;
        currentId = -1;
        importIdPool(fetchIdPool());
    }

    private List<IdSegment> fetchIdPool() {
        IdGenRequestType request = new IdGenRequestType(sequenceName,
                strategy.getSuggestedRequestSize(), strategy.getSuggestedTimeoutMillis());

        IdGenerateService service = ServiceManager.getInstance().getIdGenServiceInstance();
        if (null == service) {
            throw new RuntimeException("Get IdGenerateService failed");
        }

        IdGenResponseType response = service.fetchIdPool(request);
        if (null == response) {
            throw new RuntimeException("Get IdGenerateService response failed, sequenceName: [" + sequenceName + "]");
        }

        List<IdSegment> segments = response.getIdSegments();
        if (null == segments || segments.isEmpty()) {
            throw new RuntimeException("Get IdSegments failed, sequenceName: [" + sequenceName + "]");
        }

        return segments;
    }

    private void importIdPool(List<IdSegment> segments) {
        if (segments != null && !segments.isEmpty()) {
            for (IdSegment segment : segments) {
                this.idSegments.addLast(segment);
                initialSize += segment.getEnd().longValue() - segment.getStart().longValue() + 1;
            }
            remainedSize = initialSize;
        }
    }

    @Override
    public Number nextId() {
        IdSegment first = idSegments.peekFirst();
        if (null == first) {
            LOGGER.warn("Static pool empty, sequenceName: [" + sequenceName + "]");
            return null;
        }

        if (currentId >= first.getStart().longValue() && currentId < first.getEnd().longValue()) {
            currentId++;
        } else if (currentId < first.getStart().longValue()) {
            currentId = first.getStart().longValue();
        } else {
            idSegments.removeFirst();
            first = idSegments.peekFirst();
            if (null == first) {
                LOGGER.warn("Static pool empty, sequenceName: [" + sequenceName + "]");
                return null;
            }
            currentId = first.getStart().longValue();
        }

        remainedSize--;
        return currentId;
    }

    public long getInitialSize() {
        return initialSize;
    }

    public long getRemainedSize() {
        return remainedSize;
    }

}
