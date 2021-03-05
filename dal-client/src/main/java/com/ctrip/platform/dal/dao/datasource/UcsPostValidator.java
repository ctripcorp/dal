package com.ctrip.platform.dal.dao.datasource;

public interface UcsPostValidator {

    long drcDelayTime = 2000; // ms

    boolean validate(UcsConsistencyValidateContext context);

    default boolean dalValidate(long datasourceCreateTime, boolean isSameZone) {
        return isSameZone || (System.currentTimeMillis() - datasourceCreateTime) > drcDelayTime;
    }

}
