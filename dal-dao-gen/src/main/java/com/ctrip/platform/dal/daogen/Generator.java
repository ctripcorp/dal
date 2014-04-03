
package com.ctrip.platform.dal.daogen;

import java.util.List;

import com.ctrip.platform.dal.daogen.entity.GenTaskByFreeSql;
import com.ctrip.platform.dal.daogen.entity.GenTaskByTableViewSp;
import com.ctrip.platform.dal.daogen.entity.Progress;

public interface Generator {
	
	public boolean generateCode(int projectId, boolean regenerate,Progress progress) throws Exception;
	
	public void generateByTableView(List<GenTaskByTableViewSp> tasks,Progress progress) throws Exception;
	
	public void generateByFreeSql(List<GenTaskByFreeSql> tasks,Progress progress) throws Exception;

}

