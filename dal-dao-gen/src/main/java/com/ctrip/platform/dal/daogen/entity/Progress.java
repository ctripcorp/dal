package com.ctrip.platform.dal.daogen.entity;

public class Progress {
	
	/**
	 * 用户No
	 */
	private String userNo;
	
	/**
	 * 项目ID
	 */
	private String project_id;
	
	/**
	 * 已生成所占百分比
	 */
	private int percent;
	
	/**
	 * 已生成文件数量
	 */
	private int doneFiles;
	
	/**
	 * 需要生成的文件总数
	 */
	private int totoalFiles;
	
	/**
	 * 当前代码生成状态:isDoing,finish
	 */
	private String status;
	
	/**
	 * 其它信息
	 */
	private String otherMessage;
	
	private long time = System.currentTimeMillis();

	public String getUserNo() {
		return userNo;
	}

	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}

	public String getProject_id() {
		return project_id;
	}

	public void setProject_id(String project_id) {
		this.project_id = project_id;
	}

	public int getPercent() {
		return percent;
	}

	public void setPercent(int percent) {
		this.percent = percent;
	}

	public int getDoneFiles() {
		return doneFiles;
	}

	public void setDoneFiles(int doneFiles) {
		this.doneFiles = doneFiles;
	}

	public int getTotoalFiles() {
		return totoalFiles;
	}

	public void setTotoalFiles(int totoalFiles) {
		this.totoalFiles = totoalFiles;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getOtherMessage() {
		return otherMessage;
	}

	public void setOtherMessage(String otherMessage) {
		this.otherMessage = otherMessage;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
	
}
