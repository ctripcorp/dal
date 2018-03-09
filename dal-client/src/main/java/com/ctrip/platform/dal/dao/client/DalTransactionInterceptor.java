package com.ctrip.platform.dal.dao.client;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalCommand;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.annotation.DalTransactional;
import com.ctrip.platform.dal.dao.annotation.Shard;
import com.ctrip.platform.dal.dao.annotation.Transactional;
import com.ctrip.platform.dal.exceptions.DalException;

public class DalTransactionInterceptor implements MethodInterceptor {
    @Override
    public Object intercept(final Object obj, final Method method, final Object[] args, final MethodProxy proxy) throws Throwable {
        DalHints hints = null;
        
        for(Object o: args)
            if(o instanceof DalHints)
                hints = (DalHints)o;
        
        Annotation[][] paraAnnArrays = method.getParameterAnnotations();
        int shardParaIndex = -1;
        int i = 0;
        outter: for(Annotation[] paraAnnArray: paraAnnArrays) {
            for(Annotation paraAnn: paraAnnArray) {
                if(paraAnn instanceof Shard) {
                    shardParaIndex = i;
                    break outter;
                }
            }
            i++;
        }
        
        hints = hints == null ? new DalHints():hints.clone();
        
        if(shardParaIndex != -1) {
            Object shard = args[shardParaIndex];
            if(shard != null)
                hints.inShard(shard.toString());
        }
        
        final AtomicReference<Object> result = new AtomicReference<>();
        
        DalClientFactory.getClient(getLogicDbName(method)).execute(new DalCommand() {
            
            @Override
            public boolean execute(DalClient client) throws SQLException {
                try {
                    result.set(proxy.invokeSuper(obj, args));
                } catch (Throwable e) {
                    throw DalException.wrap(e);
                }
                return false;
            }
        }, hints);
        return result.get();
    }
    
    private String getLogicDbName(Method method) {
        DalTransactional tran = method.getAnnotation(DalTransactional.class);
        if(tran != null)
            return tran.logicDbName();

        return method.getAnnotation(Transactional.class).logicDbName();
    }
}
