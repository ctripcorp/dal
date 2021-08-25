package com.ctrip.platform.dal.dao.datasource.cluster;


import com.ctrip.platform.dal.cluster.base.HostSpec;

import java.sql.Connection;

/**
 * @author c7ch23en
 */
public interface HostConnection extends Connection {

    HostSpec getHost();

}
