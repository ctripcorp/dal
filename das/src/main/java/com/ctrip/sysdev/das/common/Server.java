package com.ctrip.sysdev.das.common;

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
