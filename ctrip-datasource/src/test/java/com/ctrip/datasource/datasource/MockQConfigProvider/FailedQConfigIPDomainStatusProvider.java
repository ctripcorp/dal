package com.ctrip.datasource.datasource.MockQConfigProvider;

import com.ctrip.datasource.datasource.IPDomainStatusChanged;
import com.ctrip.datasource.datasource.IPDomainStatusProvider;
import com.ctrip.platform.dal.common.enums.IPDomainStatus;
import com.ctrip.platform.dal.exceptions.DalException;

/**
 * Created by taochen on 2019/8/28.
 */
public class FailedQConfigIPDomainStatusProvider implements IPDomainStatusProvider {
    public static final String EXCEPTION_MESSAGE = "An error occured while getting IpDomain status from QConfig.";

    @Override
    public IPDomainStatus getStatus() {
        try {
            throw new DalException(EXCEPTION_MESSAGE);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addIPDomainStatusChangedListener(IPDomainStatusChanged callback) {

    }
}
