package com.ctrip.sysdev.das.domain;

import java.util.List;

import com.ctrip.sysdev.das.domain.enums.ActionTypeEnum;
import com.ctrip.sysdev.das.domain.enums.MessageTypeEnum;
import com.ctrip.sysdev.das.domain.param.Parameter;

/****
 * 
 * @author gawu
 * 
 */
public class RequestMessage {

	private MessageTypeEnum messageType; // always

	private ActionTypeEnum actionType; // always

	private boolean useCache; // always

	private String spName;

	private String sql;

	private List<Parameter> args;// always

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

	public List<Parameter> getArgs() {
		return args;
	}

	public void setArgs(List<Parameter> args) {
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
