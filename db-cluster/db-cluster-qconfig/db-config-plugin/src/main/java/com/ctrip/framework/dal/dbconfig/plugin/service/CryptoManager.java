package com.ctrip.framework.dal.dbconfig.plugin.service;

import com.ctrip.framework.dal.dbconfig.plugin.config.PluginConfig;
import com.ctrip.framework.dal.dbconfig.plugin.constant.MongoConstants;
import com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants;
import com.ctrip.framework.dal.dbconfig.plugin.entity.KeyInfo;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;
import java.util.Properties;


public class CryptoManager {
    //field
    private PluginConfig config;
    //加密字段: uid, password
    private static final List<String> encryptFieldList = Lists.newArrayList(
            TitanConstants.CONNECTIONSTRING_UID,
            TitanConstants.CONNECTIONSTRING_PASSWORD,
            MongoConstants.CONNECTIONSTRING_USER_ID
    );


    //constructor
    public CryptoManager(PluginConfig config) {
        this.config = config;
    }


    //encrypt raw configuration's value
    public Properties encrypt(DataSourceCrypto dataSourceCrypto, KeyService keyService, Properties properties, String sslCode) throws Exception {
        Properties retProp = new Properties();
        if (properties != null) {
            String keyServiceUri = config.getParamValue(TitanConstants.KEYSERVICE_SOA_URL);
            KeyInfo keyInfo = keyService.getKeyInfo(sslCode, keyServiceUri);
            retProp = encrypt(dataSourceCrypto, keyInfo, properties);
        }
        return retProp;
    }

    //encrypt raw configuration's value
    public Properties encrypt(DataSourceCrypto dataSourceCrypto, KeyService keyService, Properties properties) throws Exception {
        String sslCode = config.getParamValue(TitanConstants.SSLCODE);
        return encrypt(dataSourceCrypto, keyService, properties, sslCode);
    }

    //encrypt raw configuration's value
    public Properties encrypt(DataSourceCrypto dataSourceCrypto, KeyInfo keyInfo, Properties properties) throws Exception {
        Properties retProp = new Properties();
        if (properties != null) {
            String key = null;
            Object value = null;
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                key = (String) entry.getKey();
                value = entry.getValue();
                if (encryptFieldList.contains(key) && value != null) {
                    String valueStr = value.toString();
                    if (!Strings.isNullOrEmpty(valueStr)) {
                        value = dataSourceCrypto.encrypt(valueStr, keyInfo);
                    }
                }
                retProp.put(key, value);
            }

            if (!retProp.isEmpty()) {
                //设置当前加密使用的sslCode
                retProp.put(TitanConstants.SSLCODE, keyInfo.getSslCode());
            }
        }
        return retProp;
    }


    //decrypt configuration's value
    public Properties decrypt(DataSourceCrypto dataSourceCrypto, KeyService keyService, Properties properties) throws Exception {
        Properties retProp = new Properties();
        if (properties != null) {
            //find real sslCode
            String sslCode = config.getParamValue(TitanConstants.SSLCODE);
            String keyServiceUri = config.getParamValue(TitanConstants.KEYSERVICE_SOA_URL);
            String in_sslCode = (String) properties.get(TitanConstants.SSLCODE);
            if (!Strings.isNullOrEmpty(in_sslCode) && !in_sslCode.equals(sslCode)) {
                sslCode = in_sslCode;   //use inner sslCode, it is actual one
            }
            KeyInfo keyInfo = keyService.getKeyInfo(sslCode, keyServiceUri);

            String key = null;
            Object value = null;
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                key = (String) entry.getKey();
                value = entry.getValue();
                if (encryptFieldList.contains(key) && value != null) {
                    String valueStr = value.toString();
                    if (!Strings.isNullOrEmpty(valueStr)) {
                        value = dataSourceCrypto.decrypt(valueStr, keyInfo);
                    }
                }
                retProp.put(key, value);
            }
        }
        return retProp;
    }


}
