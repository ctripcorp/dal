package com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.ob;

import com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.RoundRobinStrategy;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.validator.HostValidator;
import com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.validator.HostValidatorAware;

/**
 * @Author limingdong
 * @create 2021/8/19
 */
public class ValidatorAwareRoundRobinStrategy extends RoundRobinStrategy implements HostValidatorAware {

    @Override
    public void setHostValidator(HostValidator hostValidator) {
        this.hostValidator = hostValidator;
    }
}
