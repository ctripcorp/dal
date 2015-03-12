package com.ctrip.platform.dal.dao.client;

import java.sql.SQLException;

import com.ctrip.platform.dal.catlog.CatInfo;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.markdown.MarkdownManager;
import com.ctrip.platform.dal.sql.logging.DalEventEnum;
import com.dianping.cat.Cat;
import com.dianping.cat.CatConstants;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import org.apache.commons.lang.StringUtils;

public class DalTransactionManager {
	private DalConnectionManager connManager;

	private static final ThreadLocal<DalTransaction> transactionHolder = new ThreadLocal<DalTransaction>();

	public DalTransactionManager(DalConnectionManager connManager) {
		this.connManager = connManager;
	}
	
	private int startTransaction(DalHints hints, DalEventEnum operation) throws SQLException {
		DalTransaction transaction = transactionHolder.get();

		if(transaction == null) {
			transaction = new DalTransaction( 
					getConnection(hints, true, operation), 
					connManager.getLogicDbName());
			
			transactionHolder.set(transaction);
		}
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
			transaction.validate(connManager.getLogicDbName());
			return transaction.getConnection();
		}
	}
	
	public static void clearCurrentTransaction() {
		transactionHolder.set(null);
	}

	public <T> T doInTransaction(ConnectionAction<T> action, DalHints hints)throws SQLException{
		Throwable ex = null;
		T result = null;
		int level;
		try {
			action.initLogEntry(connManager.getLogicDbName(), hints);
			action.start();
			level = startTransaction(hints, action.operation);

			result = action.execute();

			endTransaction(level);
		} catch (Throwable e) {
			ex = e;
			rollbackTransaction();
			MarkdownManager.detect(action.connHolder, action.start, e);
		}finally{
			action.populateDbMeta();
			action.cleanup();
		}

		action.end(result, ex);

		return result;
	}
}
