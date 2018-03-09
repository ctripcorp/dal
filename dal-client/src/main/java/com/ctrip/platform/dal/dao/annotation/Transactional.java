package com.ctrip.platform.dal.dao.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * To indicate that the decorated method will be performed in a transaction.
 * It can be applied to top level class or public static inner class.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Deprecated 
public @interface Transactional {
    /**
     * @return logic database name defined in dal.config/xml
     */
    String logicDbName();
}
