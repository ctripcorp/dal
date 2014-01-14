package com.ctrip.platform.daogen.gen;

import java.util.List;

import com.ctrip.platform.daogen.pojo.AutoTask;
import com.ctrip.platform.daogen.pojo.SpTask;
import com.ctrip.platform.daogen.pojo.SqlTask;

public interface Generator {
	
	public boolean generateCode(String projectId);
	
	public void generateAutoSqlCode(List<AutoTask> tasks);
	
	public void generateSPCode(List<SpTask> tasks);
	
	public void generateFreeSqlCode(List<SqlTask> tasks);

}
