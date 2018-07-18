package com.ctrip.platform.dal.dao;

public interface DalResultCallback {
	<T> void onResult(T result);
	void onError(Throwable e);
}
