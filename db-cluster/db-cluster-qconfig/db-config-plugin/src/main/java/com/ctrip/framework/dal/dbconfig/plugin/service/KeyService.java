package com.ctrip.framework.dal.dbconfig.plugin.service;


import com.ctrip.framework.dal.dbconfig.plugin.entity.KeyInfo;

public interface KeyService {

    public KeyInfo getKeyInfo(String sslCode, String keyServiceUri) throws Exception;

}
