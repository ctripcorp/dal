package com.ctrip.sysdev.das.exception;

/**
 * 
 * @author weiw
 * 
 */
public class SerDeException extends Exception {

	private static final long serialVersionUID = -3864645553436647046L;

	public SerDeException() {
		super();
	}

	public SerDeException(String message) {
		super(message);
	}

	public SerDeException(Throwable cause) {
		super(cause);
	}

	public SerDeException(String message, Throwable cause) {
		super(message, cause);
	}
}
