package com.ctrip.platform.dal.dao.helper;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

import com.ctrip.platform.dal.common.enums.ParameterDirection;
import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameter;
import com.ctrip.platform.dal.dao.StatementParameters;

public class DalStatementCreator {
	public Statement createStatement(Connection conn, DalHints hints) throws Exception {
		Statement statement = conn.createStatement(
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		
		applyHints(statement, hints);
		
		return statement;
	}

	public PreparedStatement createPreparedStatement(Connection conn, String sql, StatementParameters parameters, DalHints hints) throws Exception {
		PreparedStatement statement = conn.prepareStatement(sql,
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		
		applyHints(statement, hints);
		setParameter(statement, parameters);
		
		return statement;
	}
	
	public PreparedStatement createPreparedStatement(Connection conn, String sql, StatementParameters[] parametersList, DalHints hints) throws Exception {
		PreparedStatement statement = conn.prepareStatement(sql,
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		
		applyHints(statement, hints);
		for(StatementParameters parameters: parametersList) {
			setParameter(statement, parameters);
			statement.addBatch();
		}
		
		return statement;
	}
	
	public CallableStatement createCallableStatement(Connection conn,  String sql, StatementParameters parameters, DalHints hints) throws Exception {
		StringBuffer occupy = new StringBuffer();

//		for (int i = 0; i < parameters.size(); i++) {
//			occupy.append("?").append(",");
//		}
//
//		if (parameters.size() > 0) {
//			occupy.deleteCharAt(occupy.length() - 1);
//		}
		
//		CallableStatement statement = conn.prepareCall(String.format("{call %s(%s)}", parameters, occupy.toString()));
		CallableStatement statement = conn.prepareCall(sql);
		
		applyHints(statement, hints);
		setParameter(statement, parameters);
		registerOutParameters(statement, parameters);

		return statement;
	}

	private void setParameter(PreparedStatement statement, StatementParameters parameters) throws Exception {
		for (StatementParameter parameter: parameters.values()) {
			if(parameter.isResultsParameter())
				continue;
			if(parameter.getDirection() == null || parameter.getDirection() == ParameterDirection.InputOutput)
				setSqlParameter(statement, parameter);
		}
	}
	
	private void registerOutParameters(PreparedStatement statement, StatementParameters parameters) throws Exception {
		for (StatementParameter parameter: parameters.values()) {
			if(parameter.isOutParameter())
				setSqlParameter(statement, parameter);
		}
	}
	
	private void applyHints(Statement statement, DalHints hints) throws SQLException {
		Integer fetchSize = (Integer)hints.get(DalHintEnum.fetchSize);
		
		if(fetchSize != null && fetchSize > 0)
			statement.setFetchSize(fetchSize);

		Integer maxRows = (Integer)hints.get(DalHintEnum.maxRows);
		if (maxRows != null && maxRows > 0)
			statement.setMaxRows(maxRows);

		Integer timeout = (Integer)hints.get(DalHintEnum.timeout);
		if (timeout != null && timeout > 0)
			statement.setQueryTimeout(timeout);
	}
	
	private PreparedStatement setSqlParameter(PreparedStatement ps,
			StatementParameter parameter) throws SQLException {
		Object value = parameter.getValue();
		switch (parameter.getDbType()) {
		case Boolean:
			ps.setBoolean(parameter.getIndex(), (Boolean)value);
			break;
		case Binary:
			ps.setBytes(parameter.getIndex(), (byte[])value);
			break;
		case Byte:
			ps.setByte(parameter.getIndex(), (byte)Integer.parseInt(value.toString()));
			break;
		case DateTime:
			ps.setTimestamp(parameter.getIndex(), new Timestamp(((Date)value).getTime()));
			break;
		case Decimal:
			break;
		case Double:
			ps.setDouble(parameter.getIndex(), Double.parseDouble(value.toString()));
			break;
		case Guid:
			break;
		case Int16:
			ps.setShort(parameter.getIndex(), (short) Integer.parseInt(value.toString()));
			break;
		case Int32:
			ps.setInt(parameter.getIndex(), Integer.parseInt(value.toString()));
			break;
		case Int64:
			ps.setLong(parameter.getIndex(), Long.parseLong(value.toString()));
			break;
		case SByte:
			break;
		case Single:
			ps.setFloat(parameter.getIndex(), (float)Double.parseDouble(value.toString()));
			break;
		case String:
			ps.setString(parameter.getIndex(), value.toString());
			break;
		case StringFixedLength:
			break;
		case UInt16:
			break;
		case UInt32:
			break;
		case UInt64:
			break;
		default:
			break;
		}
		return ps;
	}
}
