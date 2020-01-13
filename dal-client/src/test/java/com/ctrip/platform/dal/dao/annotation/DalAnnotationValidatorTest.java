package com.ctrip.platform.dal.dao.annotation;

import com.ctrip.platform.dal.dao.annotation.autowire.TransactionAnnoClass;
import com.ctrip.platform.dal.dao.client.DalAnnotationValidator;
import com.ctrip.platform.dal.dao.client.DalTransactionManager;
import net.sf.cglib.proxy.*;
import org.junit.Test;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeansException;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class DalAnnotationValidatorTest {
    @Test
    public void testPostProcessBeforeInitialization() throws Exception {
        DalAnnotationValidator test = new DalAnnotationValidator();
        assertNotNull(test.postProcessBeforeInitialization(new Object(), "beanName"));        
    }
    
    @Test
    public void testValidateRawBean() throws Exception {
        DalAnnotationValidator test = new DalAnnotationValidator();
        try{
            TransactionAnnoClass bean = new TransactionAnnoClass();
            test.postProcessAfterInitialization(bean, "beanName");
            fail();
        }catch(BeanInstantiationException e) {
            assertTrue(e.getMessage().contains(DalAnnotationValidator.VALIDATION_MSG));
        }
    }
    
    @Test
    public void testValidateFactoryBean() throws Exception {
        DalAnnotationValidator test = new DalAnnotationValidator();
        try{
            TransactionAnnoClass bean = DalTransactionManager.create(TransactionAnnoClass.class);
            test.postProcessAfterInitialization(bean, "beanName");
        }catch(BeansException e) {
            fail();
        }
    }
    
    @Test
    public void testValidateFactoryBeanProxyAgain() throws Exception {
        DalAnnotationValidator test = new DalAnnotationValidator();
        try{
            TransactionAnnoClass bean = DalTransactionManager.create(TransactionAnnoClass.class);
            
            // Try to wrap it again
            
            Class targetClass = bean.getClass();
            
            Enhancer enhancer = new Enhancer();  
            enhancer.setSuperclass(targetClass);  
            enhancer.setClassLoader(targetClass.getClassLoader());
            enhancer.setCallbackFilter(new CallbackFilter(){

                @Override
                public int accept(Method method) {
                    return method.getName().startsWith("per")? 0:1;
                }});
            
            Callback[] callbacks = new Callback[]{new MethodInterceptor(){

                @Override
                public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                    return proxy.invoke(obj, args);
                }}, NoOp.INSTANCE};
            enhancer.setCallbacks(callbacks);
            bean = (TransactionAnnoClass)enhancer.create();

            
            test.postProcessAfterInitialization(bean, "beanName");
        }catch(BeansException e) {
            fail();
        }
    }
}
