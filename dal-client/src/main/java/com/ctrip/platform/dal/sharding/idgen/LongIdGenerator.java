package com.ctrip.platform.dal.sharding.idgen;

public interface LongIdGenerator extends IdGenerator {

    Long nextId();

}
