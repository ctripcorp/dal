package com.ctrip.platform.dal.dao.configure;

import com.ctrip.platform.dal.exceptions.DalException;

/**
 * Created by lilj on 2018/9/25.
 */
public interface DalInvalidConnectionString extends DalConnectionString {
    DalException getConnectionStringException();
}
