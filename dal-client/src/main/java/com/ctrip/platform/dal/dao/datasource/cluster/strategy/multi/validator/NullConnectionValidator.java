package com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.validator;

import com.ctrip.platform.dal.dao.datasource.cluster.HostConnection;

/**
 * @author c7ch23en
 */
public class NullConnectionValidator implements HostConnectionValidator {

    @Override
    public boolean validate(HostConnection connection) {
        return true;
    }

}
