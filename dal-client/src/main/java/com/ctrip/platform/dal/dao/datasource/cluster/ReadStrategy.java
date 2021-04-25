package com.ctrip.platform.dal.dao.datasource.cluster;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.exceptions.HostNotExpectedException;

import java.util.Set;

public interface ReadStrategy {

    void init(Set<HostSpec> hostSpecs);

    HostSpec pickRead(DalHints dalHints) throws HostNotExpectedException;

    void onChange(Set<HostSpec> hostSpecs);

    void dispose();
}
