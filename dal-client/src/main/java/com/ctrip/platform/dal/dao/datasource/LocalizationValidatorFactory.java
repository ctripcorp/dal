package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.cluster.config.LocalizationConfig;
import com.ctrip.platform.dal.dao.configure.ClusterInfo;

/**
 * @author c7ch23en
 */
public interface LocalizationValidatorFactory {

    LocalizationValidator createValidator(ClusterInfo clusterInfo, LocalizationConfig localizationConfig, LocalizationConfig lastLocalizationConfig);

}
