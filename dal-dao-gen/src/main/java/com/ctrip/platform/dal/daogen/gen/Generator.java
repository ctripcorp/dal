package com.ctrip.platform.dal.daogen.gen;

import java.util.List;

import com.ctrip.platform.dal.daogen.pojo.Task;

public interface Generator {
	
	public boolean generateCode(String projectId);
	
	public void generateAutoSqlCode(List<Task> tasks);
	
	public void generateSPCode(List<Task> tasks);
	
	public void generateFreeSqlCode(List<Task> tasks);

}
