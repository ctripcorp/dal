package com.ctrip.platform.dal.sql.logging;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import com.ctrip.security.encryption.AESCrypto;

public class LogEntry {
	public static final String TAG_IN_TRANSACTION = "InTransaction";
	public static final String TAG_DURATION_TIME = "DurationTime";
	public static final String TAG_DATABASE_NAME = "DatabaseName";
	public static final String TAG_ALLINONEKEY = "AllInOne";
	public static final String TAG_SERVER_ADDRESS = "ServerAddress";
	public static final String TAG_COMMAND_TYPE = "CommandType";
	public static final String TAG_USER_NAME = "UserName";
	public static final String TAG_DAO = "dao";
	public static final String TAG_RECORD_COUNT = "RecordCount";
	public static final int LOG_LIMIT = 32*1024;	
	public static final String SQLHIDDENString = "*";
	private static final String JSON_PATTERN = "{\"HasSql\":\"%s\",\"Hash\":\"%s\",\"SqlTpl\":\"%s\",\"Param\":\"%s\",\"IsSuccess\":\"%s\",\"ErrorMsg\":\"%s\", \"CostDetail\":\"%s\"}";
	
	private static Set<String> execludedClasses = null;
	private static ConcurrentHashMap<String, Integer> hashes = null;
	
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
	private boolean isSlave;
	private String serverAddress;
	private String commandType;
	private String userName;
	private int resultCount;
	private String dao;
	private String method;
	private String source;
	
	static {
		execludedClasses = new HashSet<String>();
		hashes = new ConcurrentHashMap<String, Integer>();
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

	public String getServerAddress() {
		return serverAddress;
	}

	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	public String getCommandType() {
		return commandType;
	}

	public void setCommandType() {
		if(this.event == DalEventEnum.CALL || 
				this.event == DalEventEnum.BATCH_CALL)
			this.commandType = "SP";
		else if(this.event == DalEventEnum.QUERY)
			this.commandType = "Query";
		else {
			this.commandType = "Execute";
		}
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

	public void setDao(String dao) {
		this.dao = dao;
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

	public void setSource(String source) {
		this.source = source;
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
	
	public boolean isSlave() {
		return isSlave;
	}

	public void setSlave(boolean isSlave) {
		this.isSlave = isSlave;
	}


	public int getSqlSize(){
		int size = 0;
		if(this.event == DalEventEnum.QUERY || 
				this.event == DalEventEnum.UPDATE_SIMPLE ||
				this.event == DalEventEnum.UPDATE_KH ||
				this.event == DalEventEnum.BATCH_UPDATE_PARAM){
			size = null != this.sqls && this.sqls.length > 0 ?  
					this.sqls[0].length() : 0;
		}
		if(this.event == DalEventEnum.BATCH_UPDATE){
			for(String sqll : this.sqls){
				size += sqll.length();
			}
		}
		if(this.event == DalEventEnum.CALL || 
				this.event == DalEventEnum.BATCH_CALL){
			size = this.callString.length();
		}
		
		return size;
	}
	
	private String getSqlTpl(){
		if(this.event == DalEventEnum.QUERY || 
				this.event == DalEventEnum.UPDATE_SIMPLE ||
				this.event == DalEventEnum.UPDATE_KH ||
				this.event == DalEventEnum.BATCH_UPDATE_PARAM){
			return this.sqls != null && this.sqls.length > 0 ? this.sqls[0] : "";
		}
		if(this.event == DalEventEnum.BATCH_UPDATE){
			return StringUtils.join(this.sqls, ";");
		}
		if(this.event == DalEventEnum.CALL || 
				this.event == DalEventEnum.BATCH_CALL){
			return this.callString;
		}
		
		return "";
	}
	
	private boolean hasHashCode(String sqlTpl, int hashCode){
		if(!hashes.containsKey(sqlTpl)){
			hashes.put(sqlTpl, hashCode);
			return false;
		}else{
			return true;
		}
	}

	private String getParams(){
		StringBuilder sbout = new StringBuilder();
		if(this.event == DalEventEnum.QUERY || 
				this.event == DalEventEnum.UPDATE_SIMPLE ||
				this.event == DalEventEnum.UPDATE_KH ||
				this.event == DalEventEnum.CALL){
			return null != this.pramemters && this.pramemters.length > 0 ? this.pramemters[0] : "";
		}
		if(this.event == DalEventEnum.BATCH_UPDATE_PARAM ||
				this.event == DalEventEnum.BATCH_CALL){
			for(String param : this.pramemters){
				sbout.append(param + ";");
			}
			return sbout.substring(0, sbout.length() - 1);
		}
		return "";
	}
	
	public Map<String, String> getTag() {
		
		String dbName = "";
		if(null != this.getAllInOneKey()){
			String[] tokens = this.getAllInOneKey().split("_");
			dbName += tokens != null && tokens.length > 0 ? tokens[0] : "";
		}
		dbName += this.isMaster ? ".Master" : ".Slave";
		if(this.isMaster)
			dbName += ".Master";
		if(this.isSlave)
			dbName += ".Slave";
		dbName += this.getDatabaseName() != null ? "." + this.getDatabaseName() : "";
		
		Map<String, String> tag = new LinkedHashMap<String, String>();
		tag.put(TAG_IN_TRANSACTION, this.isTransactional() ? "True" : "False");
		tag.put(TAG_DURATION_TIME, Long.toString(this.getDuration()) + "ms");
		tag.put(TAG_DATABASE_NAME, CommonUtil.null2NA(dbName));
		tag.put(TAG_ALLINONEKEY, CommonUtil.null2NA(this.getAllInOneKey()));
		tag.put(TAG_SERVER_ADDRESS, CommonUtil.null2NA(this.getServerAddress()));
		tag.put(TAG_COMMAND_TYPE, CommonUtil.null2NA(this.getCommandType()));
		tag.put(TAG_DAO, this.getDao() + "." + this.getMethod());
		tag.put(TAG_RECORD_COUNT, Long.toString(this.getResultCount()));

		return tag;
	}
	
	public String toJson(){
		String sqlTpl = this.sensitive ?  SQLHIDDENString : this.getSqlTpl();
		String params = "";
		if(this.sensitive){
			try {
				params = AESCrypto.getInstance().crypt(this.getParams());
			} catch (Exception e) {
				this.errorMsg = e.getMessage();
			}
		} else {
			params = this.getParams();
		}
		int tplLength = sqlTpl.length();
		int paramsLength = params.length();
		if(tplLength + paramsLength > LOG_LIMIT){
			sqlTpl = sqlTpl.substring(0, tplLength > LOG_LIMIT ? LOG_LIMIT : tplLength);
			params = "over long with param, can not be recorded";
		}
		int hashCode = CommonUtil.GetHashCode(sqlTpl);
		boolean existed = this.hasHashCode(sqlTpl, hashCode);
		
		try {
			params = URLEncoder.encode(params, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			this.errorMsg += Logger.getExceptionStack(e);
			params = "";
		}
		
		Logger.watcherEnd();
		Watcher wac = Logger.getAndRemoveWatcher();
		
		return String.format(JSON_PATTERN, 
				existed ? 0 : 1, 
				hashCode, 
				existed ? "" : sqlTpl, 
				params,
				this.success ? 1 : 0, 
				this.errorMsg,
				wac != null ? wac.toJson() : "{}");
	}

}
