package com.ctrip.platform.dal.dao.client;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;

import com.ctrip.platform.dal.dao.DalEventEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.annotation.DalTransactional;
import com.ctrip.platform.dal.dao.annotation.Transactional;
import com.ctrip.platform.dal.dao.markdown.MarkdownManager;
import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.exceptions.ErrorCode;

public class DalTransactionManager {
	private DalConnectionManager connManager;

	private static final ThreadLocal<DalTransaction> transactionHolder = new ThreadLocal<DalTransaction>();

	public DalTransactionManager(DalConnectionManager connManager) {
		this.connManager = connManager;
	}
	
	private <T> int startTransaction(DalHints hints, ConnectionAction<T> action) throws SQLException {
		DalTransaction transaction = transactionHolder.get();

		if(transaction == null) {
			transaction = new DalTransaction( 
					getConnection(hints, true, action.operation), 
					connManager.getLogicDbName());
			
			transactionHolder.set(transaction);
		}else{
		    transaction.validate(connManager.getLogicDbName(), connManager.evaluateShard(hints));
		}
		
        action.connHolder = transaction.getConnection();
		return transaction.startTransaction();
	}

	private void endTransaction(int startLevel) throws SQLException {
		DalTransaction transaction = transactionHolder.get();
		
		if(transaction == null)
			throw new SQLException("calling endTransaction with empty ConnectionCache");

		transaction.endTransaction(startLevel);
	}

	public static boolean isInTransaction() {
		return transactionHolder.get() != null;
	}
	
	private static void reqiresTransaction() throws DalException {
		if(!isInTransaction())
			throw new DalException(ErrorCode.TransactionNoFound);
	}
	
	public static List<DalTransactionListener> getCurrentListeners() throws DalException {
		reqiresTransaction();		
		return transactionHolder.get().getListeners();
	}
	
	public static void register(DalTransactionListener listener) throws DalException {
		reqiresTransaction();
		Objects.requireNonNull(listener, "The listener should not be null");
		
		transactionHolder.get().register(listener);
	}
	
	private void rollbackTransaction() throws SQLException {
		DalTransaction transaction = transactionHolder.get();
		
		// Already handled in deeper level
		if(transaction == null)
			return;

		transaction.rollbackTransaction();
	}
	
	public DalConnection getConnection(DalHints hints, DalEventEnum operation) throws SQLException {
		return getConnection(hints, false, operation);
	}
	
	public static String getLogicDbName() {
		return isInTransaction() ?
				transactionHolder.get().getLogicDbName() :
					null;
	}
	
	public static String getCurrentShardId() {
        return isInTransaction() ?
                transactionHolder.get().getConnection().getShardId() :
                    null;
	}

	public static DbMeta getCurrentDbMeta() {
		return isInTransaction() ?
				transactionHolder.get().getConnection().getMeta() :
					null;
	}
	
	private DalConnection getConnection(DalHints hints, boolean useMaster, DalEventEnum operation) throws SQLException {
		DalTransaction transaction = transactionHolder.get();
		
		if(transaction == null) {
			return connManager.getNewConnection(hints, useMaster, operation);
		} else {
			transaction.validate(connManager.getLogicDbName(), connManager.evaluateShard(hints));
			return transaction.getConnection();
		}
	}
	
	public static void clearCurrentTransaction() {
		transactionHolder.set(null);
	}

    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> targetClass) throws InstantiationException, IllegalAccessException {   
        Enhancer enhancer = new Enhancer();  
        enhancer.setSuperclass(targetClass);  
        enhancer.setClassLoader(targetClass.getClassLoader());
        enhancer.setCallbackFilter(new TransactionalCallbackFilter());
        Callback[] callbacks = new Callback[]{new DalTransactionInterceptor(), NoOp.INSTANCE};
        enhancer.setCallbacks(callbacks);
        enhancer.setInterfaces(new Class[]{TransactionalIntercepted.class});
        return (T)enhancer.create();
    }
    
    private static class TransactionalCallbackFilter implements CallbackFilter {
        @Override
        public int accept(Method method) {
            return method.isAnnotationPresent(DalTransactional.class) || method.isAnnotationPresent(Transactional.class) ? 0 : 1;
        }
    }
    
	public <T> T doInTransaction(ConnectionAction<T> action, DalHints hints)throws SQLException{
		action.initLogEntry(connManager.getLogicDbName(), hints);
		action.start();

		Throwable ex = null;
		T result = null;
		int level;
		try {
			level = startTransaction(hints, action);
			action.populateDbMeta();

			result = action.execute();

			endTransaction(level);
		} catch (Throwable e) {
		    action.error(e);
			rollbackTransaction();
			MarkdownManager.detect(action.connHolder, action.start, e);
		}finally{
			action.cleanup();
		}

		action.end(result);

		return result;
	}
}
