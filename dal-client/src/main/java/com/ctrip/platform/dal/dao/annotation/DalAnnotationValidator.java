package com.ctrip.platform.dal.dao.annotation;

import java.lang.reflect.Method;

import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class DalAnnotationValidator implements BeanPostProcessor {
    private static final String CGLIB_SIGNATURE = "EnhancedByCGLIB";
    
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class targetClass = bean.getClass();
        
        for(Class interf: targetClass.getInterfaces()) {
            if(interf == TransactionalIntercepted.class)
                return bean;
        }
        
        while(targetClass.getName().contains(CGLIB_SIGNATURE))
            targetClass = targetClass.getSuperclass();

        Method[] methods = targetClass.getDeclaredMethods();

        for (Method method : methods) {
            Transactional txAnnotation = method.getAnnotation(Transactional.class);
            if (txAnnotation != null) {
                throw new BeanInstantiationException(targetClass, "Bean annotated by @Transactional must be created through DalTransactionManager.create()");
            }
        }        
        
        return bean;
    }
    
    private void check(Object bean){
        
    }
}
