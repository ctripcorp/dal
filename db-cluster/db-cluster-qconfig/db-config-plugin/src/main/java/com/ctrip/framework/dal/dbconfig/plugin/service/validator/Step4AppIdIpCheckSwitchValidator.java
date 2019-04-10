package com.ctrip.framework.dal.dbconfig.plugin.service.validator;

import com.ctrip.framework.dal.dbconfig.plugin.entity.PermissionCheckEnum;
import com.dianping.cat.Cat;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Properties;

import static com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants.*;

/**
 * Step4: AppId-IP校验服务开关验证
 * [1] 允许AppId-IP校验开关 (appId.ip.check.enabled)
 * [2] 全局的AppId-IP免校验配置 (appId.ip.check.free.appIdList)
 * [3] Key内部免校验AppId列表 (freeVerifyAppIdList)
 * <p>
 * Created by lzyan on 2018/9/27,2018/12/10.
 */
public class Step4AppIdIpCheckSwitchValidator extends AbstractValidator {
    private Properties pluginProp;
    private Properties keyProp;
    private String clientAppId;

    public Step4AppIdIpCheckSwitchValidator(Properties pluginProp, Properties keyProp, String clientAppId) {
        this.pluginProp = pluginProp;
        this.keyProp = keyProp;
        this.clientAppId = clientAppId;
    }

    @Override
    public PermissionCheckEnum doValid() {
        PermissionCheckEnum result = PermissionCheckEnum.PASS;
        AbstractValidator nextValidator = getNextValidator();

        Splitter splitter = Splitter.on(",").omitEmptyStrings().trimResults();

        // [1] appIdIpCheckEnabled
        boolean appIdIpCheckEnabled = true;
        Object appIdIpCheckEnabledObj = pluginProp.get(APPID_IP_CHECK_ENABLED);
        if (appIdIpCheckEnabledObj != null) {
            appIdIpCheckEnabled = Boolean.parseBoolean(appIdIpCheckEnabledObj.toString());
        }

        // [2] appIdIpCheckFreeAppIdList    -   global config
        List<String> appIdIpCheckFreeAppIdList = Lists.newArrayList();
        String appIdIpCheckFreeAppIdListStr = pluginProp.getProperty(APPID_IP_CHECK_FREE_APPIDLIST);
        if (!Strings.isNullOrEmpty(appIdIpCheckFreeAppIdListStr)) {
            appIdIpCheckFreeAppIdList.addAll(splitter.splitToList(appIdIpCheckFreeAppIdListStr));
        }

        // [3] freeVerifyAppIdList  -   key inner config
        List<String> freeVerifyAppIdList = Lists.newArrayList();
        String freeVerifyAppIdListStr = keyProp.getProperty(FREE_VERIFY_APPID_LIST);
        if (!Strings.isNullOrEmpty(freeVerifyAppIdListStr)) {
            freeVerifyAppIdList.addAll(splitter.splitToList(freeVerifyAppIdListStr));
        }

        if (!appIdIpCheckEnabled) {
            return PermissionCheckEnum.PASS;
        } else if (appIdIpCheckFreeAppIdList.contains(clientAppId)) {
            return PermissionCheckEnum.PASS;
        } else if (freeVerifyAppIdList.contains(clientAppId)) {
            //log event for request that skip appId-IP check
            String keyName = keyProp.getProperty(CONNECTIONSTRING_KEY_NAME);
            String eName = keyName + ":" + clientAppId;
            Cat.logEvent("TitanPlugin.Permission.Valid.Key.FreeAppId", eName);
            return PermissionCheckEnum.PASS;
        } else if (nextValidator != null) {
            result = nextValidator.doValid();
        }
        return result;
    }

}
