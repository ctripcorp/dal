package com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.validator;

import com.ctrip.platform.dal.cluster.base.HostSpec;

import java.sql.Connection;
import java.util.List;
import java.util.Set;

/**
 * @Author limingdong
 * @create 2021/8/26
 */
public class MockSimpleHostValidator extends SimpleHostValidator {

    public MockSimpleHostValidator(Set<HostSpec> configuredHosts, List<HostSpec> orderHosts, long failOverTime, long blackListTimeOut, long fixedValidatePeriod) {
        super(configuredHosts, orderHosts, failOverTime, blackListTimeOut, fixedValidatePeriod);
    }

    @Override
    protected boolean doValidate(Connection connection) {
        return true;
    }
}
