package com.ctrip.platform.dal.daogen.gen;

import java.util.List;

import com.ctrip.platform.dal.daogen.pojo.GenTask;
import com.ctrip.platform.dal.daogen.pojo.GenTaskByTableViewSp;

public interface Generator {
	
	public boolean generateCode(int projectId);
	
	public void generateByTableView(List<GenTaskByTableViewSp> tasks);
	
	public void generateBySqlBuilder(List<GenTask> tasks);
	
	public void generateBySP(List<GenTask> tasks);
	
	public void generateByFreeSql(List<GenTask> tasks);

}
