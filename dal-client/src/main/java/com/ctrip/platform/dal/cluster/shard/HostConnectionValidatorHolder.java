package com.ctrip.platform.dal.cluster.shard;

import com.ctrip.platform.dal.dao.datasource.cluster.validator.HostConnectionValidator;

/**
 * @Author limingdong
 * @create 2021/8/25
 */
public interface HostConnectionValidatorHolder {

    HostConnectionValidator getHostConnectionValidator();
}