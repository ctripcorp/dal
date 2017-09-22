package com.ctrip.datasource.spring.annotation;

import com.ctrip.datasource.spring.config.DalMybatisInterceptorRegistrar;
import com.ctrip.datasource.spring.util.BeanRegistrationUtil;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

public class DalMybatisRegistrar implements ImportBeanDefinitionRegistrar {
  @Override
  public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
    AnnotationAttributes attributes =
        AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(EnableDalMybatis.class.getName()));

    boolean tracing = attributes.getBoolean("tracing");

    if (tracing) {
      boolean encryptParameters = attributes.getBoolean("encryptParameters");
      String encryptionKey = attributes.getString("encryptionKey");

      BeanRegistrationUtil.registerBeanDefinitionIfNotExists(registry, DalMybatisInterceptorRegistrar.class.getName(),
          DalMybatisInterceptorRegistrar.class, ImmutableMap.<String, Object>of("tracing", tracing, "encryptParameters",
              encryptParameters, "encryptionKey", encryptionKey));
    }
  }
}
