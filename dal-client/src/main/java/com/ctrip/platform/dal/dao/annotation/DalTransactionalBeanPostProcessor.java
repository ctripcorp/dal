package com.ctrip.platform.dal.dao.annotation;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import com.ctrip.platform.dal.dao.client.DalTransactionManager;

/**
 * Just a Spring adapter
 * 
 * @author Jerry He
 *
 */
public class DalTransactionalBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return DalTransactionManager.enable(bean);
    }
}
