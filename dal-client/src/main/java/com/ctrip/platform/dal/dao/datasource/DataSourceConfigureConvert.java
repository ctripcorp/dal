package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;

/**
 * Created by taochen on 2019/8/22.
 */
public interface DataSourceConfigureConvert {
    DataSourceConfigure desEncrypt(DataSourceConfigure dataSourceConfigure);

    DataSourceConfigure desDecrypt(DataSourceConfigure dataSourceConfigure);
}
