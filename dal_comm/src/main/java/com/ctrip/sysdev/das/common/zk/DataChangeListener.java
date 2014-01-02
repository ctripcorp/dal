package com.ctrip.sysdev.das.common.zk;

public interface DataChangeListener {
	void dataChanges();
	void valueChanges(String value);
}
