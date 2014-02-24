
package com.ctrip.platform.dal.daogen.pojo;

import java.util.List;

public class TaskAggeragation {
	
	private List<GenTaskByTableViewSp> tableViewSpTasks;
	
	private List<GenTaskBySqlBuilder> autoTasks;
	
	
	private List<GenTaskByFreeSql> sqlTasks;

	public List<GenTaskByTableViewSp> getTableViewSpTasks() {
		return tableViewSpTasks;
	}

	public void setTableViewSpTasks(List<GenTaskByTableViewSp> tableViewSpTasks) {
		this.tableViewSpTasks = tableViewSpTasks;
	}

	public List<GenTaskBySqlBuilder> getAutoTasks() {
		return autoTasks;
	}

	public void setAutoTasks(List<GenTaskBySqlBuilder> autoTasks) {
		this.autoTasks = autoTasks;
	}

	public List<GenTaskByFreeSql> getSqlTasks() {
		return sqlTasks;
	}

	public void setSqlTasks(List<GenTaskByFreeSql> sqlTasks) {
		this.sqlTasks = sqlTasks;
	}

}
