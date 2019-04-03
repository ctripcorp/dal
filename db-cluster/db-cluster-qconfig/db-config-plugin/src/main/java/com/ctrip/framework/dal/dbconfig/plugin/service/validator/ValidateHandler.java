package com.ctrip.framework.dal.dbconfig.plugin.service.validator;

import com.ctrip.framework.dal.dbconfig.plugin.entity.PermissionCheckEnum;
import com.google.common.base.Preconditions;

import java.util.Properties;

/**
 * 初始化各验证器并构建责任链
 *
 * Created by lzyan on 2018/9/27.
 */
public class ValidateHandler {
    private Properties pluginProp;
    private Properties keyProp;
    private String clientAppId;
    private String clientIp;
    private String env;

    //=== Constructor ===
    public ValidateHandler(Properties pluginProp, Properties keyProp, String clientAppId, String clientIp, String env) {
        this.pluginProp = pluginProp;
        this.keyProp = keyProp;
        this.clientAppId = clientAppId;
        this.clientIp = clientIp;
        this.env = env;
        // env 统一使用小写
        if(this.env != null) {
            this.env = this.env.toLowerCase();
        }
    }


    public PermissionCheckEnum doValid() {
        // check parameter
        Preconditions.checkArgument(pluginProp != null, "pluginProp不能为null");
        Preconditions.checkArgument(keyProp != null, "keyProp不能为null");
//        Preconditions.checkArgument(!Strings.isNullOrEmpty(clientAppId), "clientAppId不能为空");
//        Preconditions.checkArgument(!Strings.isNullOrEmpty(clientIp), "clientIp不能为空");

        // create validator
        Step1KeyEnabledValidator step1KeyEnabledValidator = new Step1KeyEnabledValidator(keyProp);
        Step2PermissionSwitchValidator step2PermissionSwitchValidator = new Step2PermissionSwitchValidator(pluginProp, keyProp, clientAppId, clientIp, env);
        Step3WhiteBlackValidator step3WhiteBlackValidator = new Step3WhiteBlackValidator(pluginProp, keyProp, clientAppId);
        Step4AppIdIpCheckSwitchValidator step4AppIdIpCheckSwitchValidator = new Step4AppIdIpCheckSwitchValidator(pluginProp, keyProp, clientAppId);
        Step5AppIdIpCheckServiceValidator step5AppIdIpCheckServiceValidator = new Step5AppIdIpCheckServiceValidator(pluginProp, keyProp, clientAppId, clientIp, env);

        //set next validator and build responsibility link
        step1KeyEnabledValidator.setNextValidator(step2PermissionSwitchValidator);
        step2PermissionSwitchValidator.setNextValidator(step3WhiteBlackValidator);
        step3WhiteBlackValidator.setNextValidator(step4AppIdIpCheckSwitchValidator);
        step4AppIdIpCheckSwitchValidator.setNextValidator(step5AppIdIpCheckServiceValidator);

        return step1KeyEnabledValidator.doValid();
    }


}
