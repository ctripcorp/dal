package com.ctrip.platform.dal.dao.helper;

import com.ctrip.platform.dal.dao.DalResultCallback;

public class DefaultResultCallback implements DalResultCallback {
	private Object result;
	
	@Override
	public <T> void onResult(T result) {
		this.result = result;
	}

	public Object getResult() {
		return result;
	}
}
