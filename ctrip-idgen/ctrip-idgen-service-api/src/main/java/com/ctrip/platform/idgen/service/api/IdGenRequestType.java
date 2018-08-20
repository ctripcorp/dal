package com.ctrip.platform.idgen.service.api;

import java.io.Serializable;

public class IdGenRequestType implements Serializable {

    private static final long serialVersionUID = 1L;

    private String sequenceName;
    private int requestSize;
    private int timeoutMillis;

    public IdGenRequestType(String sequenceName, int requestSize, int timeoutMillis) {
        this.sequenceName = sequenceName;
        this.requestSize = requestSize;
        this.timeoutMillis = timeoutMillis;
    }

    public String getSequenceName() {
        return sequenceName;
    }

    public int getRequestSize() {
        return requestSize;
    }

    public int getTimeoutMillis() {
        return timeoutMillis;
    }

}
