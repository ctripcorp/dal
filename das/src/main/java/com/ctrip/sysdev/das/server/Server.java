package com.ctrip.sysdev.das.server;

/**
 * 
 * @author weiw
 * 
 */
public interface Server {

	public void start();

	public boolean isStarted();

	public void stop();

}
