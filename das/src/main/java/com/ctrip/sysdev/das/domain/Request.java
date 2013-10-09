package com.ctrip.sysdev.das.domain;

import java.util.UUID;

public class Request extends Domain {
	private static final long serialVersionUID = 4914609946446456152L;
	private static final int CURRENT_VERSION = 1;

	private UUID taskid;
	private String credential;
	private RequestMessage message;
	
	private long decodeTime;

	public Request() {
		protocolVersion = CURRENT_VERSION;
		decodeTime = System.currentTimeMillis();
	}

	public void endDecode() {
		decodeTime = System.currentTimeMillis() - decodeTime;
	}

	public long getDecodeTime() {
		return decodeTime;
	}

	public UUID getTaskid() {
		return taskid;
	}

	public void setTaskid(UUID taskid) {
		this.taskid = taskid;
	}

	public String getCredential() {
		return credential;
	}

	public void setCredential(String credential) {
		this.credential = credential;
	}

	public RequestMessage getMessage() {
		return message;
	}

	public void setMessage(RequestMessage message) {
		this.message = message;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Request [taskid=").append(taskid).append(", dbName=")
				.append(message.getDbName()).append(", credential=").append(credential)
				.append(", message=").append(message).append("]");
		return builder.toString();
	}
}
