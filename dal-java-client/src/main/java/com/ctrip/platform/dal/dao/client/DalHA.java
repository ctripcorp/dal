package com.ctrip.platform.dal.dao.client;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;

public class DalHA {
	private int retryCount = 0;
	private boolean over = false;
	private Set<String> usedKeys = null;
	private boolean retry = false;
	private boolean failOver = false;
	private SQLException exception = null;
	private String productName = null;
	
	public DalHA(){
		this.usedKeys = new HashSet<String>();
	}
	
	public boolean isRetry() {
		return retry;
	}

	public boolean isFailOver() {
		return failOver;
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
	
	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public void update(SQLException ex){
		this.retry = false;
		this.failOver = false;
		if(this.isOver()) //There is no more connections to fail over.
			return;
		this.exception = ex;
		this.increment();
		this.retry = DalHAManager.isRetriable(this.getDBType(), this.exception);
		this.failOver = DalHAManager.isFailOverable(this.getDBType(), this.exception);
	}
	
	public void clear(){
		if(!this.isOver())
			this.exception = null;
		this.retry = false;
		this.failOver = false;
	}

	public boolean needTryAgain(){
		return !this.isOver() && null != this.exception && 
				this.retryCount < DalHAManager.getRetryCount() &&
				(this.isRetry() || this.isFailOver());
	}
	
	public void increment(){
		this.retryCount ++;
	}
	
	public int getRetryCount(){
		return this.retryCount;
	}
	
	public void addDB(String db){
		this.usedKeys.add(db);
	}
	
	public boolean contains(String db){
		return this.usedKeys.contains(db);
	}
	
	private DatabaseCategory getDBType(){
		if(this.productName.equalsIgnoreCase("Microsoft SQL Server"))
			return DatabaseCategory.SqlServer;
		else{
			return DatabaseCategory.MySql;
		}
	}
	
	public String getDB(){
		if(!this.usedKeys.isEmpty() && this.usedKeys.size() == 1)
			return this.usedKeys.iterator().next();
		else{
			return null;
		}
	}
}
