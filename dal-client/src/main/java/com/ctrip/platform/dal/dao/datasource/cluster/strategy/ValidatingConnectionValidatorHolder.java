package com.ctrip.platform.dal.dao.datasource.cluster.strategy;

import com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.validator.HostConnectionValidator;

public interface ValidatingConnectionValidatorHolder {

    HostConnectionValidator getValidatingConnectionValidator();
}
