package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;

/**
 * Created by taochen on 2019/8/22.
 */
public class DefaultDataSourceConfigureConvert implements DataSourceConfigureConvert {
    @Override
    public DataSourceConfigure desEncrypt(DataSourceConfigure unEncryptConfig) {
        return unEncryptConfig;
    }

    @Override
    public DataSourceConfigure desDecrypt(DataSourceConfigure encryptConfig) {
        return encryptConfig;
    }
}
