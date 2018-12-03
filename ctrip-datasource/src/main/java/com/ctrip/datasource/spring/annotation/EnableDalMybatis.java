package com.ctrip.datasource.spring.annotation;

import com.ctrip.platform.dal.dao.client.LoggerAdapter;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

/**
 * Use this annotation to enable dal mybatis support when using Java Config.
 *
 * <p>Configuration example:</p>
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableDalMybatis
 * public class AppConfig {
 *
 * }
 * </pre>
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(DalMybatisRegistrar.class)
public @interface EnableDalMybatis {

  /**
   * set to true to enable logging, set to false to disable.
   * @return
   */
  boolean tracing() default true;

  /**
   * set to true to encrypt parameter values when logging to cat
   */
  boolean encryptParameters() default true;

  /**
   * set a customized encryption key, please note that the key's length must be 8, e.g. abcd1234
   */
  String encryptionKey() default LoggerAdapter.DEFAULT_SECRET_KEY;
}
