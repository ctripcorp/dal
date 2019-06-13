package com.ctrip.framework.dal.dbconfig.plugin.mock;

import com.ctrip.framework.dal.dbconfig.plugin.constant.CommonConstants;
import com.ctrip.framework.dal.dbconfig.plugin.entity.AppIdIpCheckEntity;
import com.ctrip.framework.dal.dbconfig.plugin.service.AppIdIpManager;
import com.dianping.cat.Cat;

import java.util.List;

/**
 * Created by shenjie on 2019/6/12.
 */
public class MockAppIdManager extends AppIdIpManager {

    @Override
    public List<AppIdIpCheckEntity> getAllAppIdIp(String env) {
        Cat.logEvent("MockAppIdManager", "getAllAppIdIp");
        List<AppIdIpCheckEntity> appIdIps = CmsDataGenerator.generateNormalCacheAppIdIps();
        return appIdIps;
    }

    @Override
    public Integer checkAppIdIp(AppIdIpCheckEntity appIdIp) {
        Cat.logEvent("MockAppIdManager", "checkAppIdIp");
        List<AppIdIpCheckEntity> tmpCache = CmsDataGenerator.generateTmpCacheAppIdIps();
        for (AppIdIpCheckEntity cacheAppIdIp : tmpCache) {
            if (cacheAppIdIp.equals(appIdIp)) {
                return CommonConstants.PAAS_RETURN_CODE_SUCCESS;
            }
        }
        return CommonConstants.PAAS_RETURN_CODE_NOT_MATCH;
    }
}
