package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.datasource.log.OperationType;

public interface UcsPostValidator {

    long drcDelayTime = 3000; // ms

    boolean validate(UcsConsistencyValidateContext context);

    default boolean dalValidate(long datasourceCreateTime, boolean isSameZone, OperationType operationType) {
        return OperationType.QUERY.equals(operationType)
                || isSameZone || (System.currentTimeMillis() - datasourceCreateTime) > drcDelayTime;
    }

}
