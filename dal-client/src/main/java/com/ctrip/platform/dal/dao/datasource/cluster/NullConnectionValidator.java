package com.ctrip.platform.dal.dao.datasource.cluster;

/**
 * @author c7ch23en
 */
public class NullConnectionValidator implements ConnectionValidator {

    @Override
    public boolean validate(HostConnection connection) {
        return true;
    }

}
