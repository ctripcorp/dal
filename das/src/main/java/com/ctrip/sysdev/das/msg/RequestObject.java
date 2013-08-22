package com.ctrip.sysdev.das.msg;

import java.util.UUID;

public class RequestObject {
	
	public UUID taskid;
	
	public String dbName;
	
	public String credential;
	
	public MessageObject message;
	
	public int propertyCount(){
		return 4;
	}

}
