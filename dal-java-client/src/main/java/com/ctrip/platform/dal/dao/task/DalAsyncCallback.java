package com.ctrip.platform.dal.dao.task;

import java.util.concurrent.Future;

public class DalAsyncCallback {
	
	private Future<?> result = null;

	public Future<?> getFuture() {
		return this.result;
	}
	
	public void setResult(Future<?> result) {
		this.result = result;
	}
	
}
