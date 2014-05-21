package com.ctrip.platform.dal.dao.client;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import com.ctrip.platform.dal.dao.DalCommand;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.logging.DalEventEnum;
import com.ctrip.platform.dal.dao.logging.LogEntry;

public abstract class ConnectionAction<T> {
	DalEventEnum operation;
	String sql;
	String callString;
	String[] sqls;
	StatementParameters parameters;
	StatementParameters[] parametersList;
	DalCommand command;
	List<DalCommand> commands;
	ConnectionHolder connHolder;
	Connection conn;
	Statement statement;
	PreparedStatement preparedStatement;
	CallableStatement callableStatement;
	ResultSet rs;
	
	void populate(DalEventEnum operation, String sql, StatementParameters parameters) {
		this.operation = operation;
		this.sql = sql;
		this.parameters = parameters;
	}

	void populate(String[] sqls) {
		this.operation = DalEventEnum.BATCH_UPDATE;
		this.sqls = sqls;
	}
	
	void populate(String sql, StatementParameters[] parametersList) {
		this.operation = DalEventEnum.BATCH_UPDATE_PARAM;
		this.sql = sql;
		this.parametersList = parametersList;
	}
	
	void populate(DalCommand command) {
		this.operation = DalEventEnum.EXECUTE;
		this.command = command;
	}
	
	void populate(List<DalCommand> commands) {
		this.operation = DalEventEnum.EXECUTE;
		this.commands = commands;
	}
	
	void populateSp(String callString, StatementParameters parameters) {
		this.operation = DalEventEnum.CALL;
		this.callString = callString;
		this.parameters = parameters;
	}
	
	void populateSp(String callString, StatementParameters []parametersList) {
		this.operation = DalEventEnum.BATCH_CALL;
		this.callString = callString;
		this.parametersList = parametersList;
	}
	
	public void populate(LogEntry entry) {
		connHolder.getMeta().populate(entry);
	}
	
	abstract T execute() throws Exception;

}
