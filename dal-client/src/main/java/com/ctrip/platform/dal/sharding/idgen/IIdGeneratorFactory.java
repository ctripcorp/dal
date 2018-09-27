package com.ctrip.platform.dal.sharding.idgen;

public interface IIdGeneratorFactory {

    IdGenerator getIdGenerator(String sequenceName);

}
