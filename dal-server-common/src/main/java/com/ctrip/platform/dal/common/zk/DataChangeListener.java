package com.ctrip.platform.dal.common.zk;

public interface DataChangeListener {
	void dataChanges();
	void valueChanges(String value);
}
