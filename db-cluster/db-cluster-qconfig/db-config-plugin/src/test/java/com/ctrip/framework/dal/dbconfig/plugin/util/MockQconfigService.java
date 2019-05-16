package com.ctrip.framework.dal.dbconfig.plugin.util;

import com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import qunar.tc.qconfig.common.bean.PaginationResult;
import qunar.tc.qconfig.common.exception.QServiceException;
import qunar.tc.qconfig.common.util.ChecksumAlgorithm;
import qunar.tc.qconfig.plugin.ConfigDetail;
import qunar.tc.qconfig.plugin.ConfigField;
import qunar.tc.qconfig.plugin.QconfigService;

import java.util.List;
import java.util.Set;

/**
 * Created by lzyan on 2017/9/6.
 */
public class MockQconfigService implements QconfigService, TitanConstants {
    @Override
    public int batchSave(List<ConfigDetail> list, boolean isPublic, String operator, String remoteIp) throws QServiceException {
        return save(list, isPublic, operator, remoteIp);
    }

    @Override
    public int batchSave(List<ConfigDetail> list, boolean isPublic) throws QServiceException {
        return save(list, isPublic, null, null);
    }

    private int save(List<ConfigDetail> list, boolean isPublic, String operator, String remoteIp) throws QServiceException {
        Set<String> configFields = Sets.newHashSetWithExpectedSize(list.size());
        for (ConfigDetail configDetail : list) {
            ConfigField configField = configDetail.getConfigField();
            configFields.add(configField.getGroup() + "-" + configField.getDataId() + "-" + configField.getProfile());
        }

        if (configFields.size() != list.size()) {
            throw new QServiceException("QConfigService batch save failed.");
        }

        return 1;
    }

    @Override
    public PaginationResult<ConfigDetail> query(ConfigField configField, int pageNo, int pageSize) throws QServiceException {
        ConfigDetail cd = buildTestConfigDetail(configField.getGroup(), configField.getDataId(), configField.getProfile());
        List<ConfigDetail> configDetailList = Lists.newArrayList(cd);
        PaginationResult<ConfigDetail> cdPaginationResult = new PaginationResult<ConfigDetail>();
        cdPaginationResult.setData(configDetailList);
        cdPaginationResult.setPage(pageNo);
        cdPaginationResult.setPageSize(pageSize);
        cdPaginationResult.setTotal(100);
        cdPaginationResult.setTotalPage(10);
        return cdPaginationResult;
    }

    @Override
    public List<ConfigDetail> currentConfigWithoutPriority(List<ConfigField> list) throws QServiceException {
        String groupId = list.get(0).getGroup();
        if (TITAN_QCONFIG_PLUGIN_APPID.equals(groupId)) {
            String profile = "fat:";
            ConfigField configField = new ConfigField(groupId, TITAN_QCONFIG_PLUGIN_CONFIG_FILE, profile);
            ConfigDetail cd = new ConfigDetail();
            cd.setConfigField(configField);
            cd.setVersion(1L);
            cd.setContent(buildPluginConfigFileContent());
            return Lists.newArrayList(cd);
        } else {
            ConfigDetail cd = new ConfigDetail();
            cd.setConfigField(list.get(0));
            cd.setVersion(1L);
            cd.setContent(buildTestTitanKeyContent(null));
            return Lists.newArrayList(cd);
        }
    }

    @Override
    public List<ConfigDetail> currentConfigWithPriority(List<ConfigField> list) throws QServiceException {
        return currentConfigWithoutPriority(list);
    }

    @Override
    public String getClientAppid() {
        return "100007326";
    }

    @Override
    public List<ConfigDetail> getLatestConfigs(String group, String profile, String dateTimeStr) throws QServiceException {
        ConfigDetail cd = buildTestConfigDetail(group, null, profile);
        List<ConfigDetail> configDetailList = Lists.newArrayList(cd);
        return configDetailList;
    }

    //build test ConfigDetail
    private ConfigDetail buildTestConfigDetail(String group, String dataId, String profile) {
        if (Strings.isNullOrEmpty(group)) {
            group = TITAN_QCONFIG_KEYS_APPID;
        }
        if (Strings.isNullOrEmpty(dataId)) {
            dataId = "titantest_lzyan_v_01";
        }
        if (Strings.isNullOrEmpty(profile)) {
            profile = "fat:";
        }

        String content = buildTestTitanKeyContent(dataId);
        ConfigField configField = new ConfigField(group, dataId, profile);
        ConfigDetail cd = new ConfigDetail(configField);
        cd.setVersion(1L);
        cd.setContent(content);
        cd.setChecksum(ChecksumAlgorithm.getChecksum(content));
        return cd;
    }

    //build plugin itself config file
    private String buildPluginConfigFileContent() {//
        String tokenKey = "rO0ABXNyABRqYXZhLnNlY3VyaXR5LktleVJlcL35T7OImqVDAgAETAAJYWxnb3JpdGhtdAASTGphdmEvbGFuZy9TdHJpbmc7WwAHZW5jb2RlZHQAAltCTAAGZm9ybWF0cQB+AAFMAAR0eXBldAAbTGphdmEvc2VjdXJpdHkvS2V5UmVwJFR5cGU7eHB0AANSU0F1cgACW0Ks8xf4BghU4AIAAHhwAAACejCCAnYCAQAwDQYJKoZIhvcNAQEBBQAEggJgMIICXAIBAAKBgQDUDj06foXddWkhKtmJtl16Fft0UHD/WRtrZDKqfMbXkheAuB9PcRHe9DaHebBA1dODp43D4PHAsfS24ff4gdiWhdEYdF6wdV73Fn+YNwLsH/okuUCtXQ85D+QHu7fGVLFhjrU5fhKjofUxCsfmf4m4yj2h7dD+hJLRJk0JD/hZHQIDAQABAoGASiKwRUL2ifYCSxYv93VKOOR2hLOazarZazIchH4bBkKM9PNp/twI42l9pt9kP0aCLAToCxMZccTFSSq3BqpejZ3CTxetq/XSbRNIfJ7Xi5xrgioKkJoyIQ0Zy/D2Zz4I7PKc/UgoFSqnBN85sS1BZGA0jDZrGaV71YXnx1L3egECQQD9FaNnWn3D0yEQ2ZxabH35KpUnlR2fvQZWYL7jlXCSU/vvzxtmjgfueh4/8PxBo+64Wij5QPknS6o55IzBtQM/AkEA1n+aqPDZoGb6EeviZPF8lnaMd8jwlIfh7cWB7kaIqMx4Q0HUBfBCGzXk9Qul36gYWoxzeDwtOUijVCAdQh64owJAFx657cAriw8njyWCDhSpMXD9bT9HFIetI4j1B09omEWJ12+BHk5NVTDcwJSgRtLWBQtfgN25pShZZa6GWU/S+wJBAKkFYB+jujlVK9SXZYxZZe1CeSmio0DHWlZ8fgf+eI1aoaGN677KNa0vaL1XclutH5OqfQrPkGtFO758l9GUV7UCQE8hkYHZXFE1FfK9xHwiu/MSniiLUuPZulIjHsoRwc+Gb6P0Dk9zcZfpjEY76pCC/yy5SLWCx/jccYwIilZMYRl0AAZQS0NTIzh+cgAZamF2YS5zZWN1cml0eS5LZXlSZXAkVHlwZQAAAAAAAAAAEgAAeHIADmphdmEubGFuZy5FbnVtAAAAAAAAAAASAAB4cHQAB1BSSVZBVEU=";
        String returnFlag = "\n";
        StringBuilder sb = new StringBuilder();
        sb.append("needCheckDbConnection=true").append(returnFlag);
        sb.append("key.service.soa.url=https://cscmws.infosec.fws.qa.nt.ctripcorp.com/cscmws2/json/VerifySign").append(returnFlag);
        sb.append("appId.ip.check.service.url=http://paas.ctripcorp.com/api/v2/titan/verify/").append(returnFlag);
        sb.append("appId.ip.check.service.token=540e79aa2d7bfb18007fa1ced9436f6515f291dddf229ff895586aa05724ba6f").append(returnFlag);
        sb.append("appId.ip.check.service.pass.codeList=0,4").append(returnFlag);
        sb.append("sslCode=VZ00000000000441").append(returnFlag);
        sb.append("permission.pro.subenv.list=fat1").append(returnFlag);
        sb.append("titan.admin.server.list=0:0:0:0:0:0:0:1,10.5.1.174,127.0.0.1,10.32.20.124,10.32.20.3").append(returnFlag);
        sb.append("dba.connection.check.url=http://mysqlapi.db.uat.qa.nt.ctripcorp.com:8080/database/checktitanconnect").append(returnFlag);
        sb.append("http.white.list=1.1.1.1").append(returnFlag);
        sb.append("token.key=" + tokenKey).append(returnFlag);
        return sb.toString();
    }

    //build test content
    public String buildTestTitanKeyContent(String keyName) {
        if (Strings.isNullOrEmpty(keyName)) {
            keyName = "titantest_lzyan_v_01";
        }
        String returnFlag = "\n";
        StringBuilder sb = new StringBuilder();
        sb.append("sslCode=VZ00000000000441").append(returnFlag);
        sb.append("keyName=").append(keyName).append(returnFlag);
        sb.append("serverName=mysqldaltest01.mysql.db.fat.qa.nt.ctripcorp.com").append(returnFlag);
        sb.append("serverIp=10.2.74.111").append(returnFlag);
        sb.append("port=55111").append(returnFlag);
        sb.append("uid=DD326CA3D8F038641D6A7FF9D3948BD0").append(returnFlag);
        sb.append("password=1A08EF1DDB2951B79EBA0839072FBEBB02A9A77FCF74D09CCDA2ECC6C7E6C17B").append(returnFlag);
        sb.append("dbName=mysqldaltest01db").append(returnFlag);
        sb.append("providerName=MySql.Data.MySqlClient").append(returnFlag);
        sb.append("enabled=true").append(returnFlag);
        sb.append("updateUser=lzyan").append(returnFlag);
        sb.append("createUser=lzyan").append(returnFlag);
        sb.append("timeOut=30").append(returnFlag);
        sb.append("extParam=").append(returnFlag);
        sb.append("version=2").append(returnFlag);
        return sb.toString();
    }

}
