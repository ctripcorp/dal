package com.ctrip.platform.dal.sql.logging;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import com.ctrip.platform.dal.dao.DalEventEnum;
import com.ctrip.platform.dal.dao.client.DalWatcher;
import com.ctrip.platform.dal.dao.client.LogEntry;
import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.exceptions.ErrorCode;
import com.ctrip.security.encryption.AESCrypto;
import com.dianping.cat.message.Transaction;

public class CtripLogEntry extends LogEntry {
	public static final String TAG_IN_TRANSACTION = "InTransaction";
	public static final String TAG_DURATION_TIME = "DurationTime";
	public static final String TAG_DATABASE_NAME = "DatabaseName";
	public static final String TAG_ALLINONEKEY = "AllInOne";
	public static final String TAG_SERVER_ADDRESS = "ServerAddress";
	public static final String TAG_ERROR_CODE = "ErrorCode";
	public static final String TAG_COMMAND_TYPE = "CommandType";
	public static final String TAG_USER_NAME = "UserName";
	public static final String TAG_DAO = "dao";
	public static final String TAG_RECORD_COUNT = "RecordCount";
	public static final int LOG_LIMIT = 32*1024;	
	public static final String SQLHIDDENString = "*";
	private static final String JSON_PATTERN = "{\"HasSql\":\"%s\",\"Hash\":\"%s\",\"SqlTpl\":\"%s\",\"Param\":\"%s\",\"IsSuccess\":\"%s\",\"ErrorMsg\":\"%s\", \"CostDetail\":\"%s\"}";
	private static final String ERRORCDE_PATTERN = "SYS%sL%s";
	
	private static ConcurrentHashMap<String, Integer> hashes = new ConcurrentHashMap<String, Integer>();

	private Transaction catTransaction;

	public Transaction getCatTransaction() {
		return catTransaction;
	}

	public void setCatTransaction(Transaction catTransaction) {
		this.catTransaction = catTransaction;
	}

	private String getSqlTpl(){
		DalEventEnum event = getEvent();
		
		if(event == DalEventEnum.QUERY || 
				event == DalEventEnum.UPDATE_SIMPLE ||
				event == DalEventEnum.UPDATE_KH ||
				event == DalEventEnum.BATCH_UPDATE_PARAM){
			return getSqls() != null && getSqls().length > 0 ? getSqls()[0] : "";
		}
		if(event == DalEventEnum.BATCH_UPDATE){
			return StringUtils.join(getSqls(), ";");
		}
		if(event == DalEventEnum.CALL || 
				event == DalEventEnum.BATCH_CALL){
			return getCallString();
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
		DalEventEnum event = getEvent();
		String[] pramemters = getPramemters();
		
		StringBuilder sbout = new StringBuilder();
		if(pramemters == null || pramemters.length <= 0){
			return sbout.toString();
		}
		if(event == DalEventEnum.QUERY || 
				event == DalEventEnum.UPDATE_SIMPLE ||
				event == DalEventEnum.UPDATE_KH ||
				event == DalEventEnum.CALL){
			return null != pramemters && pramemters.length > 0 ? pramemters[0] : "";
		}
		if(event == DalEventEnum.BATCH_UPDATE_PARAM ||
				event == DalEventEnum.BATCH_CALL){
			for(String param : pramemters){
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
		dbName += isMaster() ? ".Master" : ".Slave";

		dbName += this.getDatabaseName() != null ? "." + this.getDatabaseName() : "";
		
		Map<String, String> tag = new LinkedHashMap<String, String>();
		tag.put(TAG_IN_TRANSACTION, this.isTransactional() ? "True" : "False");
		tag.put(TAG_DURATION_TIME, Long.toString(this.getDuration()) + "ms");
		tag.put(TAG_DATABASE_NAME, CommonUtil.null2NA(dbName));
		tag.put(TAG_ALLINONEKEY, CommonUtil.null2NA(this.getAllInOneKey()));
		tag.put(TAG_ERROR_CODE, this.getErrorCode());
		tag.put(TAG_COMMAND_TYPE, CommonUtil.null2NA(this.getCommandType()));
		tag.put(TAG_DAO, this.getDao() + "." + this.getMethod());
		tag.put(TAG_RECORD_COUNT, Long.toString(this.getResultCount()));

		return tag;
	}
	
	private String getErrorCode(){
		Throwable exception = getException();
		
		if(exception == null)
			return "NA";
		else{
			if(exception instanceof DalException){
				DalException dalEx = (DalException)exception;
				return String.format(ERRORCDE_PATTERN, 5, dalEx.getErrorCode());
			}else if(exception instanceof SQLException){
				return String.format(ERRORCDE_PATTERN, 1, "0000");
			}else{
				return String.format(ERRORCDE_PATTERN, 5, ErrorCode.Unknown.getCode());
			}
		}
	}

	public String getEncryptParameters(boolean encryptLogging){
		String params = "";
		if(isSensitive() || encryptLogging){
			try {
				params = AESCrypto.getInstance().encrypt(this.getParams());
			} catch (Exception e) {
				setErrorMsg(e.getMessage());
			}
		} else {
			params = this.getParams();
		}
		return params;
	}


	public String toJson(boolean encryptLogging){
		String sqlTpl = isSensitive() ?  SQLHIDDENString : this.getSqlTpl();
		String params = getEncryptParameters(encryptLogging);
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
			setErrorMsg(getErrorMsg() + DalCLogger.getExceptionStack(e));
			params = "";
		}
		
		return String.format(JSON_PATTERN, 
				existed ? 0 : 1, 
				hashCode, 
				sqlTpl, 
				params,
				isSuccess() ? 1 : 0, 
				CommonUtil.string2Json(getErrorMsg()),
				DalWatcher.toJson());
	}

}