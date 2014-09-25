package com.ctrip.platform.appinternals.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface BeanMeta {
	/**
	 * Set the alias name for display
	 * @return
	 * 		Alias name
	 */
	String alias() default "";
	/**
	 * If this option is set true
	 * the field is not configurable
	 * @return
	 * 		Configurable or not
	 */
	boolean omit() default false;
}
