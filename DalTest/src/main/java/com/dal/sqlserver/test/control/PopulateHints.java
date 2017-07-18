package com.dal.sqlserver.test.control;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.KeyHolder;
import com.ctrip.platform.dal.dao.helper.DefaultResultCallback;
import com.xrosstools.xunit.Context;
import com.xrosstools.xunit.Processor;

public class PopulateHints implements Processor {

	@Override
	public void process(Context ctx) {
		WebContext context = (WebContext)ctx;
		context.setHints(parseHints(context));
	}
	
	public DalHints parseHints(WebContext context) {
		DalHints hints = new DalHints();

		String a = context.get("enableKeyHolder");
		if("on".equals(a))
			hints.setKeyHolder(new KeyHolder());

		String value = context.get("shardConstrain");
		if("inShard".equals(value)) {
			value = context.get("Shard");
			if(value != null)
				hints.inShard(value);
		} else if("inAllShards".equals(value)){
			hints.inAllShards();
		} else if("inShards".equals(value)){
			Set<String> shards = new HashSet<>();
			value = context.get("Shards");
			shards.addAll(Arrays.asList(value.split(",")));
			hints.inShards(shards);
		}
		
		value = context.get("invocationMode");
		if("asynchronours".equals(value))
			hints.asyncExecution();
		else if("callback".equals(value))
			hints.callbackWith(new DefaultResultCallback());
		
		return hints;
	}
}
