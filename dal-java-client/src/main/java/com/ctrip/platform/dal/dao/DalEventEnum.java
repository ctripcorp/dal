package com.ctrip.platform.dal.dao;

public enum DalEventEnum {
	QUERY("query", 2001),
	UPDATE_SIMPLE("update", 2002),
	UPDATE_KH("update(KeyHolder)", 2003),
	BATCH_UPDATE("batchUpdate(sqls)", 2004),
	BATCH_UPDATE_PARAM("batchUpdate(params)", 2005),
	EXECUTE("execute", 2006),
	CALL("call", 2007),
	BATCH_CALL("call(params)", 2008),
	CONNECTION_SUCCESS("connection_success", 2010),
	CONNECTION_FAILED("connection_failed", 2011);
	
	private String operation;
	private int eventId;
	private DalEventEnum(String operation, int eventId) {
		this.operation = operation;
		this.eventId = eventId;
	}
	public int getEventId() {
		return eventId;
	}
	public String getOperation() {
		return operation;
	}
}
