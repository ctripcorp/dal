package com.ctrip.platform.dal.dao;

public interface QueryCallback {
	<T> void onResult(T result);
}
