package com.ctrip.platform.dal.dao.datasource.cluster.strategy;

import com.ctrip.platform.dal.dao.datasource.cluster.validator.HostValidator;
import com.ctrip.platform.dal.dao.datasource.cluster.validator.HostValidatorAware;

/**
 * @Author limingdong
 * @create 2021/8/19
 */
public class ValidatorAwareRoundRobinAccessStrategy extends RoundRobinAccessStrategy implements HostValidatorAware {

    @Override
    public void setHostValidator(HostValidator hostValidator) {
        this.hostValidator = hostValidator;
    }
}
