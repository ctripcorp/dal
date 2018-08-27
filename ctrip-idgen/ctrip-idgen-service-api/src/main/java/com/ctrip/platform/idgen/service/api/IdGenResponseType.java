package com.ctrip.platform.idgen.service.api;

import java.io.Serializable;
import java.util.List;

public class IdGenResponseType implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<IdSegment> idSegments;
    private Number singleId;

    public IdGenResponseType(List<IdSegment> idSegments) {
        this.idSegments = idSegments;
    }

    public IdGenResponseType(Number singleId) {
        this.singleId = singleId;
    }

    public List<IdSegment> getIdSegments() {
        return idSegments;
    }

    public Number getSingleId() {
        return singleId;
    }

}
