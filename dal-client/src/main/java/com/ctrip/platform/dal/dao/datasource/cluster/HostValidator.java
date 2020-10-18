package com.ctrip.platform.dal.dao.datasource.cluster;

public interface HostValidator {

    boolean available(ConnectionFactory factory, HostSpec host, RouteOptions options);
}
