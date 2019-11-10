package com.ctrip.framework.dal.dbconfig.plugin.service;

import com.ctrip.framework.dal.dbconfig.plugin.entity.KeyInfo;

public interface DataSourceCrypto{

    String encrypt(String source, KeyInfo key) throws Exception;
    String decrypt(String source, KeyInfo key) throws Exception;

}
