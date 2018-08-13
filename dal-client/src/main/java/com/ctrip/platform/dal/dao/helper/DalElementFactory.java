package com.ctrip.platform.dal.dao.helper;

import com.ctrip.platform.dal.dao.configure.DalPropertiesLocator;
import com.ctrip.platform.dal.dao.log.ILogger;

/**
 * Created by lilj on 2018/7/31.
 */
public interface DalElementFactory extends Ordered{
    DalElementFactory DEFAULT = ServiceLoaderHelper.getInstance(DalElementFactory.class);
    DalPropertiesLocator getDalPropertiesLocator();
    ILogger getILogger();
}
