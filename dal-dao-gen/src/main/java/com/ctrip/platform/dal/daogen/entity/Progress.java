package com.ctrip.platform.dal.daogen.entity;

import com.ctrip.platform.dal.daogen.resource.ProgressResource;

public class Progress {
	
	/**
	 * 用户No
	 */
	private String userNo;
	
	/**
	 * 项目ID
	 */
	private int project_id;
	
	/**
	 * 已生成所占百分比
	 */
	private int percent=0;
	
	/**
	 * 已生成文件数量
	 */
	private int doneFiles=0;
	
	/**
	 * 需要生成的文件总数
	 */
	private int totalFiles=0;
	
	/**
	 * 当前代码生成状态:isDoing,finish
	 */
	private String status = ProgressResource.ISDOING;
	
	/**
	 * 其它信息
	 */
	private String otherMessage;
	
	private long time = System.currentTimeMillis();
	
	private String random="";
	
	public synchronized String getUserNo() {
		return userNo;
	}

	public synchronized void setUserNo(String userNo) {
		this.userNo = userNo;
	}

	public synchronized int getProject_id() {
		return project_id;
	}

	public synchronized void setProject_id(int project_id) {
		this.project_id = project_id;
	}

	public synchronized int getPercent() {
		return percent;
	}

	public synchronized void setPercent(int percent) {
		this.percent = percent;
	}

	public synchronized int getDoneFiles() {
		return doneFiles;
	}

	public synchronized void setDoneFiles(int doneFiles) {
		this.doneFiles = doneFiles;
	}

	public synchronized int getTotalFiles() {
		return totalFiles;
	}

	public synchronized void setTotalFiles(int totalFiles) {
		this.totalFiles = totalFiles;
	}

	public synchronized String getStatus() {
		return status;
	}

	public synchronized void setStatus(String status) {
		this.status = status;
	}

	public synchronized String getOtherMessage() {
		return otherMessage;
	}

	public synchronized void setOtherMessage(String otherMessage) {
		this.otherMessage = otherMessage;
	}

	public synchronized long getTime() {
		return time;
	}

	public synchronized void setTime(long time) {
		this.time = time;
	}

	public synchronized String getRandom() {
		return random;
	}

	public synchronized void setRandom(String random) {
		this.random = random;
	}
	
}
