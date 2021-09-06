package com.ctrip.platform.dal.dao.datasource.cluster;

import com.ctrip.framework.dal.cluster.client.base.HostSpec;

import java.sql.Connection;

/**
 * @author c7ch23en
 */
public interface HostConnection extends Connection {

    HostSpec getHost();

}
