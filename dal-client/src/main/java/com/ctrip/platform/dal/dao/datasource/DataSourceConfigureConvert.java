package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.helper.Ordered;

/**
 * Created by taochen on 2019/8/22.
 */
public interface DataSourceConfigureConvert extends Ordered {
    DataSourceConfigure desEncrypt(DataSourceConfigure dataSourceConfigure);

    DataSourceConfigure desDecrypt(DataSourceConfigure dataSourceConfigure);
}
