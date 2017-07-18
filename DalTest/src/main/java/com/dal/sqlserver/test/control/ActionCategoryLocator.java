package com.dal.sqlserver.test.control;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.xrosstools.xunit.Context;
import com.xrosstools.xunit.Locator;

public class ActionCategoryLocator implements Locator {
	private String defaultKey;
	private Map<String, Set<String>> operationMap = new HashMap<>();

	public ActionCategoryLocator() {
		Set<String> ops = null;
		
		ops = new HashSet<>();
		ops.add("insertSingle");
		ops.add("insertMultiple");
		ops.add("batchInsert");
		operationMap.put("insert", ops);
		
		ops = new HashSet<>();
		ops.add("deleteSingle");
		ops.add("deleteMultiple");
		ops.add("batchDelete");
		operationMap.put("delete", ops);

		ops = new HashSet<>();
		ops.add("updateSingle");
		ops.add("updateMultiple");
		ops.add("batchUpdate");
		operationMap.put("update", ops);

		ops = new HashSet<>();
		ops.add("getAll");
		ops.add("deleteAll");
		ops.add("queryByPk");
		ops.add("queryByPage");
		ops.add("count");
		operationMap.put("select", ops);
		
		ops = new HashSet<>();
		ops.add("findPeople");
		ops.add("insertPeople");
		ops.add("deletePeople");
		ops.add("updatePeople");
		operationMap.put("userDefined", ops);
		
		ops = new HashSet<>();
		ops.add("timeout");
		ops.add("nontimeout");
		ops.add("decrypt");
		ops.add("checkConnection");
		operationMap.put("management", ops);

		ops = new HashSet<>();
		ops.add("markdownApp");
		ops.add("markupApp");
		ops.add("enableAutoMarkdown");
		ops.add("diableAutoMarkdown");
		ops.add("markupAllDb");
		ops.add("markdownByName");
		ops.add("markdownBothSimpleShard");
		ops.add("setAutoMarkupBatch");
		ops.add("setAutoMarkupDelay");
		ops.add("enableTimeoutMarkdown");
		ops.add("setErrorCountThreshold");
		ops.add("setSamplingDuration");
		ops.add("setErrorPercentThreshold");
		ops.add("setErrorPercentReferCount");
		ops.add("setMySqlErrorCodes");
		ops.add("setSqlServerErrorCodes");
		ops.add("markdownDbSet");
		ops.add("markupDbSet");
		operationMap.put("markdown", ops);
	}

	@Override
	public String locate(Context ctx) {
		WebContext context = (WebContext) ctx;
		String action = context.getAction();
		
		for(Map.Entry<String, Set<String>> opsEntry: operationMap.entrySet()){
			if(opsEntry.getValue().contains(action))
				return opsEntry.getKey();
		}
		return null;
	}

	@Override
	public void setDefaultKey(String key) {
		this.defaultKey = key;
	}

	@Override
	public String getDefaultKey() {
		return defaultKey;
	}
}
