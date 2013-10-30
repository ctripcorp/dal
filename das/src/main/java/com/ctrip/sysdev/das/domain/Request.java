package com.ctrip.sysdev.das.domain;

import io.netty.util.AttributeKey;

import java.util.UUID;

public class Request extends Domain {
	public static final AttributeKey<Long> DECODE_START = new AttributeKey<Long>("DECODE_START");
	
	private static final long serialVersionUID = 4914609946446456152L;
	private static final int CURRENT_VERSION = 1;

	private UUID taskid;
	private String credential;
	private RequestMessage message;
	
	private long decodeStart;
	private long decodeEnd;

	public Request() {
		protocolVersion = CURRENT_VERSION;
	}

	public void setDecodeStart(long decodeStart) {
		this.decodeStart = decodeStart;
	}

	public void endDecode(long decodeStart) {
		decodeEnd = System.currentTimeMillis();
	}

	public long getDecodeStart() {
		return decodeStart;
	}
	
	public long getDecodeEnd() {
		return decodeEnd;
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
