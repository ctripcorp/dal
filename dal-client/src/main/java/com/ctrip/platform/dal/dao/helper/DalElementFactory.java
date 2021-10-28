package com.ctrip.platform.dal.dao.helper;

import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesProvider;
import com.ctrip.platform.dal.dao.datasource.DatasourceBackgroundExecutor;
import com.ctrip.platform.dal.dao.datasource.LocalizationValidatorFactory;
import com.ctrip.platform.dal.dao.log.ILogger;
import com.ctrip.platform.dal.dao.log.TimeoutStatsLogger;

/**
 * Created by lilj on 2018/7/31.
 */
public interface DalElementFactory extends Ordered {

    DalElementFactory DEFAULT = ServiceLoaderHelper.getInstance(DalElementFactory.class);

    ILogger getILogger();

    DalPropertiesProvider getDalPropertiesProvider();

    DatasourceBackgroundExecutor getDatasourceBackgroundExecutor();

    LocalizationValidatorFactory getLocalizationValidatorFactory();

    EnvUtils getEnvUtils();

    TimeoutStatsLogger getTimeoutStatsLogger();

}
