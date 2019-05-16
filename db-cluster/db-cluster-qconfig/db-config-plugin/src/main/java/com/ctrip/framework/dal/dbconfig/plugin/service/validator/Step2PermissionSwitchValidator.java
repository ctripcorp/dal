package com.ctrip.framework.dal.dbconfig.plugin.service.validator;

import com.ctrip.framework.dal.dbconfig.plugin.entity.ClientRequestContext;
import com.ctrip.framework.dal.dbconfig.plugin.entity.PermissionCheckEnum;
import com.ctrip.framework.dal.dbconfig.plugin.util.CommonHelper;
import com.dianping.cat.Cat;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import static com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants.*;

/**
 * Step2: 权限验证开关
 *  [1] 允许权限验证总开关 (permission.valid.enabled)
 *  [2] 免验证IP列表 (permission.valid.free.ipList)
 *  [3] mhaLastUpdate 是否存在且是30分钟内 (mhaLastUpdate + 30m > Now)
 *  [4] clientAppId 是否为空
 *   [4.1] 若为空, 为空跳过是否允许 (permission.valid.skip.blank.appId.allowed)
 *   [4.2] Key内部免验证IP列表 (freeVerifyIpList)
 *
 * Created by lzyan on 2018/9/26, 2018/12/27.
 */
public class Step2PermissionSwitchValidator extends AbstractValidator {
    private Properties pluginProp;
    private Properties keyProp;
    private ClientRequestContext context;

    public Step2PermissionSwitchValidator(Properties pluginProp, Properties keyProp, ClientRequestContext context) {
        this.pluginProp = pluginProp;
        this.keyProp = keyProp;
        this.context = context;
    }

    @Override
    public PermissionCheckEnum doValid() {
        PermissionCheckEnum result = PermissionCheckEnum.PASS;
        AbstractValidator nextValidator = getNextValidator();

        Splitter splitter = Splitter.on(",").omitEmptyStrings().trimResults();

        // [1] permissionValidEnabled
        boolean permissionValidEnabled = true;
        Object permissionValidEnabledObj = pluginProp.get(PERMISSION_VALID_ENABLED);
        if(permissionValidEnabledObj != null){
            permissionValidEnabled = Boolean.parseBoolean(permissionValidEnabledObj.toString());
        }

        // [2] permissionValidFreeIpList
        List<String> permissionValidFreeIpList = Lists.newArrayList();
        String permissionValidFreeIpListStr = pluginProp.getProperty(PERMISSION_VALID_FREE_IPLIST);
        if(!Strings.isNullOrEmpty(permissionValidFreeIpListStr)){
            permissionValidFreeIpList.addAll(splitter.splitToList(permissionValidFreeIpListStr));
        }

        // [3] mhaLastUpdate
        boolean mhaUpdate = false;
        String mhaLastUpdateTimeStr = keyProp.getProperty(MHA_LAST_UPDATE_TIME);
        if(!Strings.isNullOrEmpty(mhaLastUpdateTimeStr)){
            try {
                Date mhaLastUpdateDate = CommonHelper.dfFull.get().parse(mhaLastUpdateTimeStr);
                long mhaLastUpdateMills = mhaLastUpdateDate.getTime();
                long allowIntervalMills = 30 * 60 * 1000;   //默认: 30 min
                Object mhaLastUpdateAllowIntervalMinObj = pluginProp.get(MHA_LAST_UPDATE_ALLOW_INTERVAL_MIN);
                if(mhaLastUpdateAllowIntervalMinObj != null){
                    allowIntervalMills = Long.parseLong(mhaLastUpdateAllowIntervalMinObj.toString()) * 60 * 1000;
                }
                if(mhaLastUpdateMills + allowIntervalMills > System.currentTimeMillis()) {  //在允许时间范围内则放行
                    mhaUpdate = true;
                }
            } catch (Exception e) {
                Cat.logError(e);
            }
        }

        String clientAppId = context.getAppId();
        String clientIp = context.getIp();

        if(!permissionValidEnabled) {
            try {
                // validation detect    [2018-10-19]
                new ValidateDetector(pluginProp, keyProp, context).detect();
            } catch (Exception e) {
                Cat.logError(e);
            }
            return PermissionCheckEnum.PASS;
        } else if(permissionValidFreeIpList.contains(clientIp)) {
            //log event for free valid ip of request
            Cat.logEvent("TitanPlugin.Permission.Valid.FreeIp", clientIp);
            return PermissionCheckEnum.PASS;
        } else if(mhaUpdate) {
            //log event for mhaUpdate
            String keyName = keyProp.getProperty(CONNECTIONSTRING_KEY_NAME);
            String eventName = String.format("%s#%s#%s", keyName, mhaLastUpdateTimeStr, clientAppId);
            Cat.logEvent("TitanPlugin.Permission.Valid.Free.MHAUpdate", eventName);
            return PermissionCheckEnum.PASS;
        } else if(Strings.isNullOrEmpty(clientAppId)) {
            //log event for no appId request
            String keyName = keyProp.getProperty(CONNECTIONSTRING_KEY_NAME);
            String eventName = clientIp + ":" + keyName;
            Cat.logEvent("TitanPlugin.Permission.Valid.NoAppId", eventName);

            // [4.1] permValidSkipBlankAppIdAllowed
            boolean permValidSkipBlankAppIdAllowed = true;
            Object permValidSkipBlankAppIdAllowedObj = pluginProp.get(PERMISSION_VALID_SKIP_BLANK_APPID_ALLOWED);
            if(permValidSkipBlankAppIdAllowedObj != null){
                permValidSkipBlankAppIdAllowed = Boolean.parseBoolean(permValidSkipBlankAppIdAllowedObj.toString());
            }
            if(permValidSkipBlankAppIdAllowed) {
                return PermissionCheckEnum.PASS;
            } else {
                // [4.2] freeVerifyIpList
                List<String> freeVerifyIpList = Lists.newArrayList();
                String freeVerifyIpListStr = keyProp.getProperty(FREE_VERIFY_IPLIST);
                if(!Strings.isNullOrEmpty(freeVerifyIpListStr)){
                    freeVerifyIpList.addAll(splitter.splitToList(freeVerifyIpListStr));
                }
                if(freeVerifyIpList.contains(clientIp)) {
                    //log event for free valid ip of request
                    String eName = keyName + ":" + clientIp;
                    Cat.logEvent("TitanPlugin.Permission.Valid.Key.FreeIp", eName);
                    return PermissionCheckEnum.PASS;
                } else {
                    return PermissionCheckEnum.FAIL_APPID_BLANK;
                }
            }
        } else if(nextValidator != null) {
            result = nextValidator.doValid();
        }
        return result;
    }

}
