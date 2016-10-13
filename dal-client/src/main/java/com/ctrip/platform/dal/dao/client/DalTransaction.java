package com.ctrip.platform.dal.dao.client;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.exceptions.ErrorCode;

public class DalTransaction  {
	private String logicDbName;
	private DalConnection connHolder;
	private List<DalTransactionListener> listeners;
	private int level = 0;
	private boolean rolledBack = false;
	private boolean completed = false;
	private DalLogger logger;
	
	public DalTransaction(DalConnection connHolder, String logicDbName) throws SQLException{
		this.logicDbName = logicDbName;
		this.connHolder = connHolder;
		connHolder.getConn().setAutoCommit(false);
		this.logger = DalClientFactory.getDalLogger();
	}
	
	public void validate(String logicDbName) throws SQLException {
		if(logicDbName == null || logicDbName.length() == 0)
			throw new DalException(ErrorCode.LogicDbEmpty);
		
		if(!logicDbName.equals(this.logicDbName))
			throw new DalException(ErrorCode.TransactionDistributed, this.logicDbName, logicDbName);
	}
	
	public String getLogicDbName() {
		return logicDbName;
	}

	public DalConnection getConnection() {
		return connHolder;
	}
	
	public void register(DalTransactionListener listener) {
		if(listeners == null)
			listeners = new ArrayList<>();
			
			listeners.add(listener);
	}
	
	public List<DalTransactionListener> getListeners() {
		return listeners;
	}
	
	public int getLevel() {
		return level;
	}
	
	public boolean isRolledBack() {
		return rolledBack;
	}

	public int startTransaction() throws SQLException {
		if(rolledBack || completed)
			throw new DalException(ErrorCode.TransactionState);
		
		return level++;
	}
	
	public void endTransaction(int startLevel) throws SQLException {
		if(rolledBack || completed)
			throw new DalException(ErrorCode.TransactionState);

		if(startLevel != (level - 1)) {
			rollbackTransaction();
			throw new DalException(ErrorCode.TransactionLevelMatch, (level - 1), startLevel);
		}
		
		if(--level == 0) {
			completed = true;
			cleanup(true);
		}
	}
	
	public void rollbackTransaction() throws SQLException {
		if(rolledBack)
			return;

		rolledBack = true;
		// Even the rollback fails, we still set the flag to true;
		cleanup(false);
	}
	
	private void cleanup(boolean commit) {
		Connection conn = connHolder.getConn();
		try {
			if(commit){
				beforeCommit();
				conn.commit();
				afterCommit();
			}else {
				beforeRollback();
				conn.rollback();
				afterRollback();
			}
		} catch (Throwable e) {
			logger.error("Can not commit or rollback on current connection", e);
		}

		try {
			conn.setAutoCommit(true);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		connHolder.close();
		DalTransactionManager.clearCurrentTransaction();
	}
	
	private void beforeCommit() {
		if(listeners == null)
			return;
		
		for(DalTransactionListener listener: listeners) {
			try{
				listener.beforeCommit();
			}catch(Throwable e) {
				logError(e);
			}
		}
	}

	private void beforeRollback() {
		if(listeners == null)
			return;
		
		for(DalTransactionListener listener: listeners) {
			try{
				listener.beforeRollback();
			}catch(Throwable e) {
				logError(e);
			}
		}
	}
	private void afterCommit() {
		if(listeners == null)
			return;
		
		for(DalTransactionListener listener: listeners) {
			try{
				listener.afterCommit();
			}catch(Throwable e) {
				logError(e);
			}
		}
	}
	private void afterRollback() {
		if(listeners == null)
			return;
		
		for(DalTransactionListener listener: listeners) {
			try{
				listener.afterRollback();
			}catch(Throwable e) {
				logError(e);
			}
		}
	}
	
	private void logError(Throwable e) {
		try {
			logger.error(e.getMessage(), e);
		} catch (Throwable e2) {
			System.err.println(e2);
		}
	}
}
