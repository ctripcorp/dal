package com.ctrip.platform.dal.dao;

import java.util.List;
import java.util.Map;

public class DaoCommand {
	private String sql;
	private List<StatementParameter> parameters;
	private Map<DaoHintEnum, Object> hints;
	
	public DaoCommand(String sql, List<StatementParameter> parameters, Map<DaoHintEnum, Object> hints) {
		this.sql = sql;
		this.parameters = parameters;
		this.hints = hints;
	}
	
	public String getSql() {
		return sql;
	}

	public List<StatementParameter> getParameters() {
		return parameters;
	}

	public Map<DaoHintEnum, Object> getHints() {
		return hints;
	}
}
