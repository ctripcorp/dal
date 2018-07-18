package com.ctrip.platform.dal.dao.annotation;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Target(TYPE) 
@Retention(RUNTIME)
public @interface Database {
    String name();
}