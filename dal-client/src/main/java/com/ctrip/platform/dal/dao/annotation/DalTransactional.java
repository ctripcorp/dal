package com.ctrip.platform.dal.dao.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DalTransactional {
    /**
     * @return logic database name defined in dal.config/xml
     */
    String logicDbName();
}
