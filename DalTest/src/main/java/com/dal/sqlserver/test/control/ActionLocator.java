package com.dal.sqlserver.test.control;

import com.xross.tools.xunit.Context;
import com.xross.tools.xunit.Locator;

/**
 * To simplify the sample, this is not used
 * @author jhhe
 *
 */
public class ActionLocator implements Locator {
	private String defaultKey;
	@Override
	public String locate(Context ctx) {
		WebContext context = (WebContext)ctx;
		
		return context.getAction();
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
