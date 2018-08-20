package com.ctrip.platform.idgen.client;

import com.ctrip.platform.idgen.service.api.IdSegment;

import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

public class IdPool {

    private Deque<IdSegment> idSegments = new ConcurrentLinkedDeque<>();
    private long initialSize = 0;
    private long currentId = -1;
    private long remainedSize = 0;

    private String sequenceName;
    private PoolManageStrategy manageStrategy;

    public IdPool(final List<IdSegment> segments, String sequenceName) {
        this(segments, null, new DefaultPoolManageStrategy());
    }

    public IdPool(final List<IdSegment> segments, String sequenceName, PoolManageStrategy manageStrategy) {
        importPairs(segments);
        this.sequenceName = sequenceName;
        this.manageStrategy = manageStrategy;
    }

    private void importPairs(final List<IdSegment> segments) {
        if (segments != null && segments.size() > 0) {
            for (IdSegment segment : segments) {
                if (validateNextPair(this.idSegments.peekLast(), segment)) {
                    this.idSegments.addLast(segment);
                    remainedSize += segment.getEnd().longValue() - segment.getStart().longValue() + 1;
                }
            }
            initialSize = remainedSize;
        }
    }

    private boolean validateNextPair(final IdSegment last, final IdSegment next) {
        if (null == next) {
            return false;
        }
        if (next.getStart().longValue() < 0 || next.getEnd().longValue() < 0 ||
                next.getEnd().longValue() < next.getStart().longValue()) {
            return false;
        }
        /* check fallback
        if (null != last && next.getStart() <= last.getEnd()) {
            return false;
        }*/
        return true;
    }

    public synchronized Number getId() {
        IdSegment first = idSegments.peekFirst();
        if (null == first) {
            throw new RuntimeException("id pool empty");
        }

        if (currentId >= first.getStart().longValue() && currentId < first.getEnd().longValue()) {
            currentId++;
        } else if (currentId < first.getStart().longValue()) {
            currentId = first.getStart().longValue();
        } else {
            idSegments.removeFirst();
            first = idSegments.peekFirst();
            if (null == first) {
                throw new RuntimeException("id pool empty");
            }
            currentId = first.getStart().longValue();
        }

        remainedSize--;
        return currentId;
    }

    public void extendPool(final List<IdSegment> segments) {
        importPairs(segments);
    }

    public long getInitialSize() {
        return initialSize;
    }

    public long getRemainedSize() {
        return remainedSize;
    }

    public String getSequenceName() {
        return sequenceName;
    }

    public PoolManageStrategy getManageStrategy() {
        return manageStrategy;
    }

}
