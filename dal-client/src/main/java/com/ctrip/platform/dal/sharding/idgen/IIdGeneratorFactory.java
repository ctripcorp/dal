package com.ctrip.platform.dal.sharding.idgen;

import com.ctrip.platform.dal.dao.helper.Ordered;

public interface IIdGeneratorFactory extends Ordered {

    IdGenerator getIdGenerator(String sequenceName);

}
