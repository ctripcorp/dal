package com.ctrip.sysdev.das.domain;

import java.util.List;

import com.ctrip.sysdev.das.domain.enums.OperationType;
import com.ctrip.sysdev.das.domain.enums.StatementType;

/****
 * 
 * @author gawu
 * 
 */
public class RequestMessage {
	private String dbName;

	private StatementType statementType; // always

	private OperationType operationType; // always

	private boolean useCache; // always

	private String spName;

	private String sql;

	private List<StatementParameter> args;// always

	private int flags; // always

	
	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public StatementType getStatementType() {
		return statementType;
	}

	public void setStatementType(StatementType statementType) {
		this.statementType = statementType;
	}

	public OperationType getOperationType() {
		return operationType;
	}

	public void setOperationType(OperationType operationType) {
		this.operationType = operationType;
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

	public List<StatementParameter> getArgs() {
		return args;
	}

	public void setArgs(List<StatementParameter> args) {
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
