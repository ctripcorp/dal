package com.ctrip.platform.dal.dao.datasource.cluster;

/**
 * @author c7ch23en
 */
public interface RequestContext {

    String clientZone();

    boolean isWriteOperation();

}
