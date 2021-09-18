package com.ctrip.platform.dal.dao.datasource.cluster.strategy;

import com.ctrip.platform.dal.dao.datasource.cluster.strategy.multi.validator.HostConnectionValidator;

/**
 * @Author limingdong
 * @create 2021/8/25
 */
public interface HostConnectionValidatorHolder {

    HostConnectionValidator getHostConnectionValidator();
}