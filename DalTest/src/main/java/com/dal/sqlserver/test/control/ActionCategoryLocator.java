package com.dal.sqlserver.test.control;

import com.xross.tools.xunit.Context;
import com.xross.tools.xunit.Locator;

public class ActionCategoryLocator implements Locator {
	private String defaultKey;
	@Override
	public String locate(Context ctx) {
		WebContext context = (WebContext)ctx;
		
		return null;
	}

	@Override
	public void setDefaultKey(String key) {
		this.defaultKey = defaultKey;
	}

	@Override
	public String getDefaultKey() {
		return defaultKey;
	}

}
