package com.ctrip.platform.dal.dao.datasource.cluster;

/**
 * @author c7ch23en
 */
public class DefaultRequestContext implements RequestContext {

    private final String clientZone;

    public DefaultRequestContext(String clientZone) {
        this.clientZone = clientZone;
    }

    @Override
    public String clientZone() {
        return clientZone;
    }

    @Override
    public boolean isWriteOperation() {
        return true;
    }

}
