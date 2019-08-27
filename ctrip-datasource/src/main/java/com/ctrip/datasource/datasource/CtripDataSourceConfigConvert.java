package com.ctrip.datasource.datasource;

import com.ctrip.datasource.util.DalEncrypter;
import com.ctrip.platform.dal.dao.client.LoggerAdapter;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.datasource.DataSourceConfigureConvert;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.log.ILogger;

/**
 * Created by taochen on 2019/8/22.
 */
public class CtripDataSourceConfigConvert implements DataSourceConfigureConvert{
    protected static final ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();

    private DalEncrypter dalEncrypter = null;

    @Override
    public DataSourceConfigure desEncrypt(DataSourceConfigure dataSourceConfigure) {
        dalEncrypter = getEncrypter();
        DataSourceConfigure encryptDataSourceConfigure = dataSourceConfigure.clone();
        String userName = dalEncrypter.desEncrypt(dataSourceConfigure.getUserName());
        String password = dalEncrypter.desEncrypt(dataSourceConfigure.getPassword());
        encryptDataSourceConfigure.setUserName(userName);
        encryptDataSourceConfigure.setPassword(password);
        return encryptDataSourceConfigure;
    }

    @Override
    public DataSourceConfigure desDecrypt(DataSourceConfigure dataSourceConfigure) {
        dalEncrypter = getEncrypter();
        DataSourceConfigure decryptDataSourceConfigure = dataSourceConfigure.clone();
        String userName = dalEncrypter.desDecrypt(dataSourceConfigure.getUserName());
        String password = dalEncrypter.desDecrypt(dataSourceConfigure.getPassword());
        decryptDataSourceConfigure.setUserName(userName);
        decryptDataSourceConfigure.setPassword(password);
        return decryptDataSourceConfigure;
    }

    private synchronized DalEncrypter getEncrypter() {
        if (dalEncrypter == null) {
            try {
                dalEncrypter = new DalEncrypter(LoggerAdapter.DEFAULT_SECRET_KEY);
            } catch (Throwable e) {
                LOGGER.warn("DalEncrypter initialization failed.");
            }
        }
        return dalEncrypter;
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }
}
