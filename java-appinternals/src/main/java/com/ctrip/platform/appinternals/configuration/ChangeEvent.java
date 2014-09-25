package com.ctrip.platform.appinternals.configuration;

public interface ChangeEvent {
	void before(Object oldVal, String newVal) throws Exception;
	void end(Object oldVal, String newVal) throws Exception;
}
