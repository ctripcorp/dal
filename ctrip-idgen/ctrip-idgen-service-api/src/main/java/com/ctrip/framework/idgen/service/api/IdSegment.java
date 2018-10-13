package com.ctrip.framework.idgen.service.api;

import java.io.Serializable;

public class IdSegment implements Serializable {

    private static final long serialVersionUID = 1L;

    // id range: [start, end]
    private Number start;
    private Number end;

    public IdSegment(Number start, Number end) {
        this.start = start;
        this.end = end;
    }

    public Number getStart() {
        return start;
    }

    public Number getEnd() {
        return end;
    }

}
