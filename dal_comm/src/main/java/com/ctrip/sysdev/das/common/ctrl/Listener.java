package com.ctrip.sysdev.das.common.ctrl;

public interface Listener {
	void onMessage(Message msg);
	void onError(Error error);
}
