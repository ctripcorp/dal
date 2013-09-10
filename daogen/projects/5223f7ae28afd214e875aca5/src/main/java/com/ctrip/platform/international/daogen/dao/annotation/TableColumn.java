package com.ctrip.platform.international.daogen.dao.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.FIELD)
public @interface TableColumn {
	
	String columnName();

}
