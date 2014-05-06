
package com.ctrip.platform.dal.daogen;

import java.util.List;
import java.util.Map;

import com.ctrip.platform.dal.daogen.entity.GenTaskByFreeSql;
import com.ctrip.platform.dal.daogen.entity.GenTaskByTableViewSp;
import com.ctrip.platform.dal.daogen.entity.Progress;

public interface Generator {
	
	public boolean prepareDirectory(int projectId, boolean regenerate);
	
	public boolean prepareData(int projectId, boolean regenerate, Progress progress);

	public boolean generateCode(int projectId, Progress progress, Map hints);

	public boolean clearResource();

}

