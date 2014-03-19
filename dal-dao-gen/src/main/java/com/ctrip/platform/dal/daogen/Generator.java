
package com.ctrip.platform.dal.daogen;

import java.util.List;

import com.ctrip.platform.dal.daogen.entity.GenTaskByFreeSql;
import com.ctrip.platform.dal.daogen.entity.GenTaskByTableViewSp;

public interface Generator {
	
	public boolean generateCode(int projectId, boolean regenerate) throws Exception;
	
	public void generateByTableView(List<GenTaskByTableViewSp> tasks) throws Exception;
	
	public void generateByFreeSql(List<GenTaskByFreeSql> tasks) throws Exception;

}

