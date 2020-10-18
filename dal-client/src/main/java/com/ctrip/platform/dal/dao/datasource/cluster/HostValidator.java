package com.ctrip.platform.dal.dao.datasource.cluster;

import com.ctrip.platform.dal.exceptions.DalException;

import java.util.Properties;

public interface HostValidator {

    boolean available(ConnectionFactory factory, HostSpec host);
}
