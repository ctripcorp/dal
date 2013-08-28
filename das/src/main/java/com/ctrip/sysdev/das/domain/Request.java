package com.ctrip.sysdev.das.domain;

import java.util.UUID;

import com.ctrip.sysdev.das.domain.msg.Message;

public class Request extends BaseDomain {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4914609946446456152L;

	private static final Request request = new Request();

	/**
	 * Initialize the protocol version to 1
	 */
	private Request() {
		protocolVersion = 1;
	}

	/**
	 * Get the default instance, mainly used to get the protocol version
	 * 
	 * @return
	 */
	public static Request getInstance() {
		return request;
	}

	public static Request getNewInstance() {
		return new Request();
	}

	private UUID taskid;

	private String dbName;

	private String credential;

	private Message message;

	/**
	 * @return the taskid
	 */
	public UUID getTaskid() {
		return taskid;
	}

	/**
	 * @param taskid
	 *            the taskid to set
	 */
	public void setTaskid(UUID taskid) {
		this.taskid = taskid;
	}

	/**
	 * @return the dbName
	 */
	public String getDbName() {
		return dbName;
	}

	/**
	 * @param dbName
	 *            the dbName to set
	 */
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	/**
	 * @return the credential
	 */
	public String getCredential() {
		return credential;
	}

	/**
	 * @param credential
	 *            the credential to set
	 */
	public void setCredential(String credential) {
		this.credential = credential;
	}

	/**
	 * @return the message
	 */
	public Message getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(Message message) {
		this.message = message;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Request [taskid=").append(taskid).append(", dbName=")
				.append(dbName).append(", credential=").append(credential)
				.append(", message=").append(message).append("]");
		return builder.toString();
	}

}
