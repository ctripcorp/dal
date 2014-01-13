package com.ctrip.platform.dao.client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ctrip.platform.dao.param.StatementParameter;
import com.ctrip.sysdev.das.common.db.DasConfigureReader;
import com.ctrip.sysdev.das.common.db.DruidDataSourceWrapper;

public class DirectClient implements Client {
	private String logicDbName;

	private DruidDataSourceWrapper connPool;

	public DirectClient(DasConfigureReader reader, String logicDbName) throws Exception {
		this.logicDbName = logicDbName;
		initConnPool(reader);
	}
	
	private void initConnPool(DasConfigureReader reader) throws Exception {
		connPool = new DruidDataSourceWrapper(reader, new String[]{logicDbName});
	}

	public String getLogicDbName() {
		return logicDbName;
	}

	private PreparedStatement createSqlStatement(Connection conn, String sql, List<StatementParameter> parameters) throws Exception {
		PreparedStatement statement = conn.prepareStatement(sql,
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		
		setParameter(statement, parameters);
		
		return statement;
	}
	
	private PreparedStatement createSpStatement(Connection conn,  String sql, List<StatementParameter> parameters) throws Exception {
		StringBuffer occupy = new StringBuffer();

		for (int i = 0; i < parameters.size(); i++) {
			occupy.append("?").append(",");
		}

		if (parameters.size() > 0) {
			occupy.deleteCharAt(occupy.length() - 1);
		}
		
		PreparedStatement statement = conn.prepareCall(String.format("{call %s(%s)}", parameters, occupy.toString()));
		setParameter(statement, parameters);
		
		return statement;
	}

	private void setParameter(PreparedStatement statement, List<StatementParameter> parameters) throws Exception {
		for (int i = 0; i < parameters.size(); i++) {
			setSqlParameter(statement, parameters.get(i));
		}

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

	
	@Override
	public ResultSet fetch(String sql, List<StatementParameter> parameters,
			Map keywordParameters) {
		Connection conn;
		try {
			conn = connPool.getConnection(logicDbName, isMaster(keywordParameters), true);
			PreparedStatement statement = createSqlStatement(conn, sql, parameters);
		
			return statement.executeQuery();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public int execute(String sql, List<StatementParameter> parameters,
			Map keywordParameters) {
		Connection conn;
		try {
			conn = connPool.getConnection(logicDbName, isMaster(keywordParameters), true);
			PreparedStatement statement = createSqlStatement(conn, sql, parameters);
		
			return statement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public ResultSet fetchBySp(String sql, List<StatementParameter> parameters,
			Map keywordParameters) {
		Connection conn;
		try {
			conn = connPool.getConnection(logicDbName, isMaster(keywordParameters), true);
			PreparedStatement statement = createSpStatement(conn, sql, parameters);
		
			return statement.executeQuery();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public int executeSp(String sql, List<StatementParameter> parameters,
			Map keywordParameters) {
		Connection conn;
		try {
			conn = connPool.getConnection(logicDbName, isMaster(keywordParameters), true);
			PreparedStatement statement = createSqlStatement(conn, sql, parameters);
		
			return statement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	private boolean isMaster(Map keywordParameters) {
		if (null != keywordParameters && keywordParameters.size() > 0
				&& keywordParameters.containsKey("master")) {
			return Boolean.getBoolean(keywordParameters.get("master")
					.toString());
		}
		
		return false;
	}
}
