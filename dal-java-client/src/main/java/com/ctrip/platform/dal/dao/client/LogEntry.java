package com.ctrip.platform.dal.dao.client;

import java.util.HashSet;
import java.util.Set;

import com.ctrip.platform.dal.dao.DalEventEnum;

public class LogEntry {
	private static Set<String> execludedClasses = null;
	
	private boolean sensitive;
	private String[] sqls;
	private String[] pramemters;
	private String callString;
	private DalEventEnum event;
	private String errorMsg;
	private boolean success;
	private boolean transactional;
	private long duration;
	private String databaseName;
	private String allInOneKey;
	private boolean isMaster;
	private String shardId;
	private String serverAddress;
	private String dbUrl;
	private String commandType;
	private String userName;
	private int resultCount;
	private String dao;
	private String method;
	private String source;
	private String clientVersion;

	private Throwable exception;
	
	private long createTime = System.currentTimeMillis();
	
	static {
		execludedClasses = new HashSet<String>();
		execludedClasses.add("com.ctrip.platform.dal.dao.client.ConnectionAction");
		execludedClasses.add("com.ctrip.platform.dal.dao.client.DalConnectionManager");
		execludedClasses.add("com.ctrip.platform.dal.dao.client.DalTransactionManager");
		execludedClasses.add("com.ctrip.platform.dal.dao.client.DalDirectClient");
		execludedClasses.add("com.ctrip.platform.dal.dao.DalTableDao");
		execludedClasses.add("com.ctrip.platform.dal.dao.DalQueryDao");
	}
	
	public LogEntry(){
		StackTraceElement[] callers = Thread.currentThread().getStackTrace();
		for (int i = 4; i < callers.length; i++) {
			StackTraceElement caller = callers[i];
			if (execludedClasses.contains(caller.getClassName()))
				continue;
			
			dao = caller.getClassName();
			method = caller.getMethodName();
			source = caller.toString();
			break;
		}
	}
	
	public void setEvent(DalEventEnum event) {
		this.event = event;
		
		String commandType;
		
		if(this.event == DalEventEnum.CALL || 
				this.event == DalEventEnum.BATCH_CALL) {
			commandType = "SP";
		} else if(this.event == DalEventEnum.QUERY) {
			commandType = "Query";
		} else {
			commandType = "Execute";
		}
		
		setCommandType(commandType);
	}

	public boolean isSensitive() {
		return sensitive;
	}

	public void setSensitive(boolean sensitive) {
		this.sensitive = sensitive;
	}
	
	public void setCallString(String callString) {
		this.callString = callString;
	}
	
	public String[] getSqls() {
		return sqls;
	}

	public void setSqls(String... sqls) {
		this.sqls = sqls;
	}

	public String[] getPramemters() {
		return pramemters;
	}

	public void setPramemters(String... pramemters) {
		this.pramemters = pramemters;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public Throwable getException() {
		return exception;
	}

	public void setException(Throwable exception) {
		this.exception = exception;
	}
	
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public boolean isTransactional() {
		return transactional;
	}

	public void setTransactional(boolean transactional) {
		this.transactional = transactional;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}

	public String getServerAddress() {
		return serverAddress;
	}

	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	public String getCommandType() {
		return commandType;
	}

	public void setCommandType(String commandType) {
		this.commandType = commandType;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getResultCount() {
		return resultCount;
	}

	public void setResultCount(int resultCount) {
		this.resultCount = resultCount;
	}

	public String getCallString() {
		return callString;
	}

	public DalEventEnum getEvent() {
		return event;
	}
	
	public String getDao() {
		return dao;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getSource() {
		return source;
	}

	public String getAllInOneKey() {
		return allInOneKey;
	}

	public void setAllInOneKey(String allInOneKey) {
		this.allInOneKey = allInOneKey;
	}

	public boolean isMaster() {
		return isMaster;
	}

	public void setMaster(boolean isMaster) {
		this.isMaster = isMaster;
	}
	
	public void setShardId(String shardId) {
		this.shardId = shardId;
	}
	
	public String getShardId() {
		return shardId;
	}

	public String getDbUrl() {
		return dbUrl;
	}

	public String getClientVersion() {
		return clientVersion;
	}

	public void setClientVersion(String clientVersion) {
		this.clientVersion = clientVersion;
	}
	
	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public int getSqlSize() {
		int size = 0;
		if (this.event == DalEventEnum.QUERY
				|| this.event == DalEventEnum.UPDATE_SIMPLE
				|| this.event == DalEventEnum.UPDATE_KH
				|| this.event == DalEventEnum.BATCH_UPDATE_PARAM) {
			size = null != this.sqls && this.sqls.length > 0 ? this.sqls[0].length() : 0;
		}
		if (this.event == DalEventEnum.BATCH_UPDATE) {
			for (String sqll : this.sqls) {
				size += sqll.length();
			}
		}
		if (this.event == DalEventEnum.CALL
				|| this.event == DalEventEnum.BATCH_CALL) {
			size = this.callString.length();
		}

		return size;
	}
}
