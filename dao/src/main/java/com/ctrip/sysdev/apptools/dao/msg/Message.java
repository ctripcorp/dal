package com.ctrip.sysdev.apptools.dao.msg;

import java.util.List;

import com.ctrip.sysdev.apptools.dao.enums.ActionTypeEnum;
import com.ctrip.sysdev.apptools.dao.enums.MessageTypeEnum;

/****
 * 
 * @author gawu
 * 
 */
public class Message {

	private MessageTypeEnum messageType; // always

	private ActionTypeEnum actionType; // always

	private boolean useCache; // always

	private String spName;

	private String sql;

	private List<List<AvailableType>> args;// always

	private int flags; // always

	public MessageTypeEnum getMessageType() {
		return messageType;
	}

	public void setMessageType(MessageTypeEnum messageType) {
		this.messageType = messageType;
	}

	public ActionTypeEnum getActionType() {
		return actionType;
	}

	public void setActionType(ActionTypeEnum actionType) {
		this.actionType = actionType;
	}

	public boolean isUseCache() {
		return useCache;
	}

	public void setUseCache(boolean useCache) {
		this.useCache = useCache;
	}

	public String getSpName() {
		return spName;
	}

	public void setSpName(String spName) {
		this.spName = spName;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public List<List<AvailableType>> getArgs() {
		return args;
	}

	public void setArgs(List<List<AvailableType>> args) {
		this.args = args;
	}

	public int getFlags() {
		return flags;
	}

	public void setFlags(int flags) {
		this.flags = flags;
	}

	/****
	 * 
	 * @return
	 */
	public int propertyCount() {
		return 6;
	}

}
