package com.ctrip.platform.dal.daogen.entity;

/**
 * Used to record the execute result for logging
 * @author wcyuan
 * @version 2014-05-13
 */
public class ExecuteResult {
	private String taskName;
	private boolean successal;
	
	public ExecuteResult(String name){
		this.taskName = name;
	}
	
	public boolean isSuccessal(){
		return this.successal;
	}
	
	public void setSuccessal(boolean success){
		this.successal = success;
	}
	
	public String getTaskName(){
		return this.taskName;
	}
}
