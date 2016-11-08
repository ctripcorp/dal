package com.ctrip.platform.dal.dao.client;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.status.DalStatusManager;

public class DalHA {
	private int retryCount = 0;
	private boolean over = false;
	private Set<String> usedKeys = null;
	private boolean retry = false;
	private SQLException exception = null;
	private DatabaseCategory dbCategory = null;
	
	public DalHA(){
		this.usedKeys = new HashSet<String>();
	}
	
	public boolean isRetry() {
		return retry;
	}
	
	public SQLException getException() {
		return this.exception;
	}

	public boolean isOver() {
		return over;
	}

	public void setOver(boolean over) {
		this.over = over;
	}
	
	public DatabaseCategory getDatabaseCategory() {
		return dbCategory;
	}

	public void setDatabaseCategory(DatabaseCategory dbCategory) {
		this.dbCategory = dbCategory;
	}

	public void update(SQLException ex){
		this.retry = false;
		if(this.isOver()) //There is no more connections to fail over.
			return;
		this.exception = ex;
		this.increment();
		if(dbCategory == DatabaseCategory.SqlServer)
			this.retry = DalStatusManager.getHaStatus()
				.getSqlservercodes().contains(this.exception.getErrorCode());
		else{
			this.retry = DalStatusManager.getHaStatus()
					.getMysqlcodes().contains(this.exception.getErrorCode());
		}
	}
	
	public void clear(){
		if(!this.isOver())
			this.exception = null;
		this.retry = false;
	}

	public boolean needTryAgain(){
		return !this.isOver() && null != this.exception && 
				this.retryCount < DalStatusManager.getHaStatus().getRetryCount() && 
				this.isRetry();
	}
	
	public void increment(){
		this.retryCount ++;
	}
	
	public int getRetryCount(){
		return this.retryCount;
	}
	
	public DalHA addDB(String db){
		this.usedKeys.add(db);
		return this;
	}
	
	public boolean contains(String db){
		return this.usedKeys.contains(db);
	}
	
	public String getDB(){
		if(!this.usedKeys.isEmpty() && this.usedKeys.size() == 1)
			return this.usedKeys.iterator().next();
		else{
			return null;
		}
	}
}
