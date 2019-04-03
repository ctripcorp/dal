package com.ctrip.framework.dal.dbconfig.plugin.service.validator;

import com.ctrip.framework.dal.dbconfig.plugin.entity.PermissionCheckEnum;

import java.util.Properties;

import static com.ctrip.framework.dal.dbconfig.plugin.constant.TitanConstants.ENABLED;

/**
 * Step1: 验证 Key 是否是启用状态
 * Created by lzyan on 2018/9/26.
 */
public class Step1KeyEnabledValidator extends AbstractValidator {
    //titan key file properties
    private Properties keyProp;

    public Step1KeyEnabledValidator(Properties keyProp) {
        this.keyProp = keyProp;
    }

    @Override
    public PermissionCheckEnum doValid() {
        PermissionCheckEnum result = PermissionCheckEnum.PASS;
        AbstractValidator nextValidator = getNextValidator();
        boolean enabled = true;
        Object enabledObj = keyProp.get(ENABLED);
        if(enabledObj != null){
            enabled = Boolean.parseBoolean(enabledObj.toString());
        }
        if(!enabled) {
            return PermissionCheckEnum.FAIL_KEY_DISABLED;
        } else if(nextValidator != null) {
            result = nextValidator.doValid();
        }
        return result;
    }

}
