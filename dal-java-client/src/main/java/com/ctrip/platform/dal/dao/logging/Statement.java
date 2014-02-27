package com.ctrip.platform.dal.dao.logging;

import com.ctrip.platform.dal.dao.StatementParameters;

public class Statement {
    private String dao;
    private String method;
    private StatementParameters params;
    private boolean sensitive;
    private long duration;
    private String sqlText;
    private int sqlHash;
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
	public StatementParameters getParams() {
		return params;
	}
	public void setParams(StatementParameters params) {
		this.params = params;
	}
	public boolean isSensitive() {
		return sensitive;
	}
	public void setSensitive(boolean sensitive) {
		this.sensitive = sensitive;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	public String getSqlText() {
		return sqlText;
	}
	public void setSqlText(String sqlText) {
		this.sqlText = sqlText;
	}
	public int getSqlHash() {
		return sqlHash;
	}
	public void setSqlHash(int sqlHash) {
		this.sqlHash = sqlHash;
	}
    
    
}
