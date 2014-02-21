package com.ctrip.platform.dal.daogen.pojo;

import java.util.List;

public class TaskAggeragation {
	
	private List<GenTaskBySqlBuilder> autoTasks;
	
	private List<GenTaskBySP> spTasks;
	
	private List<GenTaskByFreeSql> sqlTasks;

	public List<GenTaskBySqlBuilder> getAutoTasks() {
		return autoTasks;
	}

	public void setAutoTasks(List<GenTaskBySqlBuilder> autoTasks) {
		this.autoTasks = autoTasks;
	}

	public List<GenTaskBySP> getSpTasks() {
		return spTasks;
	}

	public void setSpTasks(List<GenTaskBySP> spTasks) {
		this.spTasks = spTasks;
	}

	public List<GenTaskByFreeSql> getSqlTasks() {
		return sqlTasks;
	}

	public void setSqlTasks(List<GenTaskByFreeSql> sqlTasks) {
		this.sqlTasks = sqlTasks;
	}

}
