package com.ctrip.platform.appinternals.configuration;

public interface ChangeEvent {
	void callback(String oldVal, String newVal) throws Exception;
}
