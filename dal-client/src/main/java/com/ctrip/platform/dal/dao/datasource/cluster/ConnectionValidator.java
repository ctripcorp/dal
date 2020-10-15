package com.ctrip.platform.dal.dao.datasource.cluster;

import java.sql.Connection;
import java.util.Set;

/**
 * @author c7ch23en
 */
public interface ConnectionValidator {

    boolean validate(Connection connection, Set<HostSpec> configuredHosts);

}
