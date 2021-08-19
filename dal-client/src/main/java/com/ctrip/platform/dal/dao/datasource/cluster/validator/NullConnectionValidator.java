package com.ctrip.platform.dal.dao.datasource.cluster.validator;

import com.ctrip.platform.dal.dao.datasource.cluster.HostConnection;
import com.ctrip.platform.dal.dao.datasource.cluster.validator.ConnectionValidator;

/**
 * @author c7ch23en
 */
public class NullConnectionValidator implements ConnectionValidator {

    @Override
    public boolean validate(HostConnection connection) {
        return true;
    }

}
