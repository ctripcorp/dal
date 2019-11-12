package com.ctrip.framework.dal.dbconfig.plugin.service.validator;

import com.ctrip.framework.dal.dbconfig.plugin.entity.PermissionCheckEnum;

/**
 * Created by lzyan on 2018/9/26.
 */
public abstract class AbstractValidator {
    private AbstractValidator nextValidator;

    public abstract PermissionCheckEnum doValid();

    //setter/getter
    public AbstractValidator getNextValidator() {
        return nextValidator;
    }

    public void setNextValidator(AbstractValidator nextValidator) {
        this.nextValidator = nextValidator;
    }

}
