package com.ctrip.framework.dal.dbconfig.plugin.service.validator;

import com.ctrip.framework.dal.dbconfig.plugin.entity.ClientRequestContext;
import com.dianping.cat.Cat;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Properties;

import static com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants.*;

/**
 * Created by lzyan on 2018/10/19.
 */
public class ValidateDetector {

    private Properties pluginProp;
    private Properties keyProp;
    private ClientRequestContext context;

    //=== Constructor ===
    public ValidateDetector(Properties pluginProp, Properties keyProp, ClientRequestContext context) {
        this.pluginProp = pluginProp;
        this.keyProp = keyProp;
        this.context = context;
    }

    /**
     * detect and log cat
     *  [1] blank appId
     *  [2] not in permissions
     *  [3] appId-Ip not match
     */
    public void detect() {
        try {
            // according to switch 'permission.valid.detect.enabled' to decide detect or not
            boolean permissionValidDetectEnabled = false;
            Object permissionValidDetectEnabledObj = pluginProp.get(PERMISSION_VALID_DETECT_ENABLED);
            if(permissionValidDetectEnabledObj != null) {
                permissionValidDetectEnabled = Boolean.parseBoolean(permissionValidDetectEnabledObj.toString());
            }

            String clientAppId = context.getAppId();
            String clientIp = context.getIp();
            if(permissionValidDetectEnabled) {
                // [1] blank appId
                if (Strings.isNullOrEmpty(clientAppId)) {
                    String keyName = keyProp.getProperty(CONNECTIONSTRING_KEY_NAME);
                    String eventName = clientIp + ":" + keyName;
                    Cat.logEvent("TitanPlugin.Permission.Valid.NoAppId", eventName);
                } else {
                    // [2] not in permissions
                    Step3WhiteBlackValidator step3WhiteBlackValidator = new Step3WhiteBlackValidator(pluginProp, keyProp, clientAppId);
                    step3WhiteBlackValidator.doValid();

                    // [3] appId-Ip not match
                    Splitter splitter = Splitter.on(",").omitEmptyStrings().trimResults();
                    // [3.1] appIdIpCheckFreeAppIdList    -   global config
                    List<String> appIdIpCheckFreeAppIdList = Lists.newArrayList();
                    String appIdIpCheckFreeAppIdListStr = pluginProp.getProperty(APPID_IP_CHECK_FREE_APPIDLIST);
                    if(!Strings.isNullOrEmpty(appIdIpCheckFreeAppIdListStr)){
                        appIdIpCheckFreeAppIdList.addAll(splitter.splitToList(appIdIpCheckFreeAppIdListStr));
                    }

                    // [3.2] freeVerifyAppIdList  -   key inner config
                    List<String> freeVerifyAppIdList = Lists.newArrayList();
                    String freeVerifyAppIdListStr = keyProp.getProperty(FREE_VERIFY_APPID_LIST);
                    if(!Strings.isNullOrEmpty(freeVerifyAppIdListStr)){
                        freeVerifyAppIdList.addAll(splitter.splitToList(freeVerifyAppIdListStr));
                    }

                    if(appIdIpCheckFreeAppIdList.contains(clientAppId)) {
                        //do nothing
                    } else if(freeVerifyAppIdList.contains(clientAppId)) {
                        //log event for request that skip appId-IP check
                        String keyName = keyProp.getProperty(CONNECTIONSTRING_KEY_NAME);
                        String eName = keyName + ":" + clientAppId;
                        Cat.logEvent("TitanPlugin.Permission.Valid.Key.FreeAppId", eName);
                    } else {
                        Step5AppIdIpCheckServiceValidator step5AppIdIpCheckServiceValidator = new Step5AppIdIpCheckServiceValidator(pluginProp, keyProp, context);
                        step5AppIdIpCheckServiceValidator.doValid();
                    }
                }
            }
        } catch (Exception e) {
            Cat.logError("detect(): validation detect error!", e);
        }
    }

}
