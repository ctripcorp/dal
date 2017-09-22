package com.ctrip.datasource.spring.config;

import com.ctrip.platform.dal.dao.client.LoggerAdapter;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.w3c.dom.Element;

import com.google.common.base.Strings;

public class NamespaceHandler extends NamespaceHandlerSupport {

  @Override
  public void init() {
    registerBeanDefinitionParser("mybatis", new BeanParser());
  }

  static class BeanParser extends AbstractSingleBeanDefinitionParser {
    @Override
    protected Class<?> getBeanClass(Element element) {
      return DalMybatisInterceptorRegistrar.class;
    }

    @Override
    protected boolean shouldGenerateId() {
      return true;
    }

    @Override
    protected void doParse(Element element, BeanDefinitionBuilder builder) {
      if (element.getLocalName().equals("mybatis")) {
        String tracingAttr = element.getAttribute("tracing");

        boolean tracing = Strings.isNullOrEmpty(tracingAttr) ? true : Boolean.valueOf(tracingAttr.trim());
        builder.addPropertyValue("tracing", tracing);

        if (tracing) {
          String encryptParametersAttr = element.getAttribute("encrypt-parameters");
          String encryptionKeyAttr = element.getAttribute("encryption-key");

          boolean encryptParameters =
              Strings.isNullOrEmpty(encryptParametersAttr) ? true : Boolean.valueOf(encryptParametersAttr.trim());

          String encryptionKey =
              Strings.isNullOrEmpty(encryptionKeyAttr) ? LoggerAdapter.DEFAULT_SECERET_KEY : encryptionKeyAttr.trim();

          builder.addPropertyValue("encryptParameters", encryptParameters);
          builder.addPropertyValue("encryptionKey", encryptionKey);
        }
      }
    }
  }
}
