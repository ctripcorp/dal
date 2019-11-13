package com.ctrip.framework.dal.dbconfig.plugin.mock;

import com.ctrip.framework.dal.dbconfig.plugin.constant.CommonConstants;
import com.ctrip.framework.dal.dbconfig.plugin.entity.AppIdIpCheckEntity;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * Created by shenjie on 2019/6/12.
 */
public class CmsDataGenerator {

    private static final String CMS_GET_GROUP_URL = "http://osg.ops.ctripcorp.com/api/CMSFATGetGroup/?_version=new";
    private static final String FAT_ENV = "fat";
    private static final String CMS_ACCESS_TOKEN = "96ddbe67728bc756466a226ec050456d";
    private static final int TIMEOUT = 1000;
    private static final List<String> PASS_CODES = Lists.newArrayList("0", "4");

    private static List<String> generateAppIds() {
        List<String> appIds = Lists.newArrayList();
        appIds.add("111111");
        appIds.add("222222");
        return appIds;
    }

    private static List<String> generateNormalCacheIps() {
        List<String> ips = Lists.newArrayList();
        ips.add("1.1.1.1");
        ips.add("1.1.1.2");
        ips.add("1.1.1.3");
        return ips;
    }

    private static List<String> generateTmpCacheIps() {
        List<String> ips = Lists.newArrayList();
        ips.add("1.1.1.4");
        ips.add("1.1.1.5");
        return ips;
    }

    public static List<AppIdIpCheckEntity> generateNormalCacheAppIdIps() {
        List<String> appIds = generateAppIds();
        List<String> ips = generateNormalCacheIps();
        return generateAppIdIps(appIds, ips);
    }

    public static List<AppIdIpCheckEntity> generateTmpCacheAppIdIps() {
        List<String> appIds = generateAppIds();
        List<String> ips = generateTmpCacheIps();
        return generateAppIdIps(appIds, ips);
    }

    public static List<AppIdIpCheckEntity> generateNotMarchAppIdIps() {
        List<AppIdIpCheckEntity> appIdIps = Lists.newArrayList();
        AppIdIpCheckEntity appIdIp = new AppIdIpCheckEntity();
        appIdIp.setClientAppId("333333");
        appIdIp.setClientIp("1.1.1.6");
        appIdIp.setEnv(FAT_ENV);
        appIdIp.setServiceUrl(CMS_GET_GROUP_URL);
        appIdIp.setServiceToken(CMS_ACCESS_TOKEN);
        appIdIp.setTimeoutMs(TIMEOUT);
        appIdIp.setPassCodeList(PASS_CODES);
        appIdIps.add(appIdIp);
        return appIdIps;
    }

    public static Map<AppIdIpCheckEntity, Integer> generateAppIdIpAndReturnCodes() {
        Map<AppIdIpCheckEntity, Integer> appIdIpAndReturnCodes = Maps.newHashMap();
        List<AppIdIpCheckEntity> normalCache = generateNormalCacheAppIdIps();
        for (AppIdIpCheckEntity appIdIp : normalCache) {
            appIdIpAndReturnCodes.put(appIdIp, CommonConstants.PAAS_RETURN_CODE_SUCCESS);
        }

        List<AppIdIpCheckEntity> tmpCache = generateTmpCacheAppIdIps();
        for (AppIdIpCheckEntity appIdIp : tmpCache) {
            appIdIpAndReturnCodes.put(appIdIp, CommonConstants.PAAS_RETURN_CODE_SUCCESS);
        }

        List<AppIdIpCheckEntity> notMatchAppIdIps = generateNotMarchAppIdIps();
        for (AppIdIpCheckEntity appIdIp : notMatchAppIdIps) {
            appIdIpAndReturnCodes.put(appIdIp, CommonConstants.PAAS_RETURN_CODE_NOT_MATCH);
        }

        return appIdIpAndReturnCodes;
    }

    private static List<AppIdIpCheckEntity> generateAppIdIps(List<String> appIds, List<String> ips) {
        List<AppIdIpCheckEntity> appIdIps = Lists.newArrayList();
        for (String appId : appIds) {
            for (String ip : ips) {
                AppIdIpCheckEntity appIdIp = new AppIdIpCheckEntity();
                appIdIp.setClientAppId(appId);
                appIdIp.setClientIp(ip);
                appIdIp.setEnv(FAT_ENV);
                appIdIp.setServiceUrl(CMS_GET_GROUP_URL);
                appIdIp.setServiceToken(CMS_ACCESS_TOKEN);
                appIdIp.setTimeoutMs(TIMEOUT);
                appIdIp.setPassCodeList(PASS_CODES);

                appIdIps.add(appIdIp);
            }
        }
        return appIdIps;
    }
}
