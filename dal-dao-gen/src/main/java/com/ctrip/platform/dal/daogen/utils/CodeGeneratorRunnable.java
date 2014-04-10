package com.ctrip.platform.dal.daogen.utils;

public class CodeGeneratorRunnable implements Runnable {
	
	private CodeGeneratorCallback _callback;
	public CodeGeneratorRunnable(CodeGeneratorCallback callback)
	{
		_callback = callback;
	}

	@Override
	public void run() {
		_callback.doWork();
	}

}
