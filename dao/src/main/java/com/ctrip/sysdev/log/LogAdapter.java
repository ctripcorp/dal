package com.ctrip.sysdev.log;

public interface LogAdapter {
	
	public void fatal(String message);
	
	public void error(String message);
	
	public void warn(String message);
	
	public void info(String message);
	
	public void debug(String message);
	
	public void trace(String message);

}
