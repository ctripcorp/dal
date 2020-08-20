package com.ctrip.platform.dal.dao.configure;

/**
 * @author c7ch23en
 */
public interface DalLocalConnectionString extends DalConnectionString {

    boolean tableShardingDisabled();

}
