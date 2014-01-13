package com.ctrip.platform.daogen.pojo;

import java.util.List;

public class TaskAggeragation {
	
	private List<AutoTask> autoTasks;
	
	private List<SpTask> spTasks;
	
	private List<SqlTask> sqlTasks;

	public List<AutoTask> getAutoTasks() {
		return autoTasks;
	}

	public void setAutoTasks(List<AutoTask> autoTasks) {
		this.autoTasks = autoTasks;
	}

	public List<SpTask> getSpTasks() {
		return spTasks;
	}

	public void setSpTasks(List<SpTask> spTasks) {
		this.spTasks = spTasks;
	}

	public List<SqlTask> getSqlTasks() {
		return sqlTasks;
	}

	public void setSqlTasks(List<SqlTask> sqlTasks) {
		this.sqlTasks = sqlTasks;
	}

}
