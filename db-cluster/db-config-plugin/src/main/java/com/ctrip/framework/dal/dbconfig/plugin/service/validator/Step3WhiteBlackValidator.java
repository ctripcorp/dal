package com.ctrip.framework.dal.dbconfig.plugin.service.validator;

import com.ctrip.framework.dal.dbconfig.plugin.entity.PermissionCheckEnum;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;

import java.util.List;
import java.util.Properties;

import static com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants.CONNECTIONSTRING_KEY_NAME;
import static com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants.PERMISSIONS;
import static com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants.PERMISSION_VALID_FREE_APPIDLIST;

/**
 * Step3: 白名单/黑名单 验证
 *  [1] 检查 clientAppId 是否在免验证白名单(permission.valid.free.appIdList)中, 如果在则此步骤通过流转到下一检查步骤
 *  [2] 白名单验证检查, 白名单必须配置(传进来的permission字段优先使用的是父环境Key的Permission, 参考:TitanKeyDecryptHookPlugin)
 *  [3] 黑名单验证逻辑取消
 *
 * Created by lzyan on 2018/9/26,2018/11/26,2018/11/27.
 */
public class Step3WhiteBlackValidator extends AbstractValidator {
    private Properties pluginProp;
    private Properties keyProp;
    private String clientAppId;

    public Step3WhiteBlackValidator(Properties pluginProp, Properties keyProp, String clientAppId) {
        this.pluginProp = pluginProp;
        this.keyProp = keyProp;
        this.clientAppId = clientAppId;
    }

    @Override
    public PermissionCheckEnum doValid() {
        PermissionCheckEnum result = PermissionCheckEnum.PASS;
        AbstractValidator nextValidator = getNextValidator();

        Splitter splitter = Splitter.on(",").omitEmptyStrings().trimResults();

        boolean inFreeAppIdList = checkInFreeAppIdList(splitter);
        if(!inFreeAppIdList) {
            // white check (白名单使用 permissions 字段)
            String appIdWhiteListStr = (String)keyProp.get(PERMISSIONS);   //eg: 111111,222222    //WHITE_LIST
            if(!Strings.isNullOrEmpty(appIdWhiteListStr)) {
                List<String> appIdList = splitter.splitToList(appIdWhiteListStr);
                if(!appIdList.contains(clientAppId)) {
                    result = PermissionCheckEnum.FAIL_WHITE_LIST;
                }
            } else {
                result = PermissionCheckEnum.FAIL_WHITE_LIST;
            }
        }


        if(result != PermissionCheckEnum.PASS) {
            String keyName = keyProp.getProperty(CONNECTIONSTRING_KEY_NAME);
            String eventName = String.format("%s:%s", keyName, clientAppId);
            String msg = "permissionCheckEnum=" + result + ", clientIp=" + clientAppId;
            Cat.logEvent("TitanPlugin.Permission.Valid.Fail.WhiteList", eventName, Event.SUCCESS, msg);
            return result;
        } else if(nextValidator != null) {
            result = nextValidator.doValid();
        }
        return result;
    }

    // check clientAppId in freeAppIdList or not
    private boolean checkInFreeAppIdList(Splitter splitter) {
        boolean inFreeAppIdList = false;
        String permissionValidFreeAppIdList = (String)pluginProp.get(PERMISSION_VALID_FREE_APPIDLIST);   //eg: 111111,222222
        if(!Strings.isNullOrEmpty(permissionValidFreeAppIdList)) {
            List<String> appIdList = splitter.splitToList(permissionValidFreeAppIdList);
            if(appIdList.contains(clientAppId)) {
                inFreeAppIdList = true;
            }
        }
        return inFreeAppIdList;
    }

}
