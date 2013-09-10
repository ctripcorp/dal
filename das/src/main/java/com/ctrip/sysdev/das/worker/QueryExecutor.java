package com.ctrip.sysdev.das.worker;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.sysdev.das.commons.DataSourceWrapper;
import com.ctrip.sysdev.das.domain.RequestMessage;
import com.ctrip.sysdev.das.domain.Response;
import com.ctrip.sysdev.das.domain.enums.ActionTypeEnum;
import com.ctrip.sysdev.das.domain.enums.MessageTypeEnum;
import com.ctrip.sysdev.das.domain.enums.ParameterType;
import com.ctrip.sysdev.das.domain.enums.ResultTypeEnum;
import com.ctrip.sysdev.das.domain.param.Parameter;
import com.ctrip.sysdev.das.domain.param.ParameterFactory;

public class QueryExecutor implements LogConsts {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private DataSourceWrapper dataSource;
	private RequestMessage message;

	public QueryExecutor() {
	}

	public QueryExecutor(DataSourceWrapper dataSource, RequestMessage message) {
		this.dataSource = dataSource;
		this.message = message;
	}

	public Response execute() {
		return execute(dataSource, message);
	}

	public Response execute(DataSourceWrapper dataSource, RequestMessage message) {
		Response resp = new Response();
		Connection conn = null;
		PreparedStatement statement = null;

		long start = System.currentTimeMillis();
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			
			statement = createStatement(conn, message);

			if (message.getActionType() == ActionTypeEnum.SELECT) {
				executeQuery(resp, statement);
			} else {
				boolean batchOperation = false;
				for (Parameter p : message.getArgs()) {
					if (p.getParameterType() == ParameterType.PARAMARRAY) {
						batchOperation = true;
						break;
					}
				}
				if (batchOperation) {
					executeBatch(resp, statement);
				} else {
					executeUpdate(resp, statement);
				}
				conn.commit();
			}
		} catch (Exception e) {
			logger.error(QUERY_EXECUTION_EXCEPTION, e);
		} finally {
			cleanUp(resp, conn, statement, start);
		}

		return resp;
	}

	private PreparedStatement createStatement(Connection conn,
			RequestMessage message) throws Exception {
		// TODO: add batch operation
		List<Parameter> params = message.getArgs();

		PreparedStatement statement = null;

		if (message.getMessageType() == MessageTypeEnum.SQL) {
			statement = conn.prepareStatement(message.getSql());
		} else {
			StringBuffer occupy = new StringBuffer();

			for (int i = 0; i < params.size(); i++) {
				occupy.append("?").append(",");
			}

			occupy.deleteCharAt(occupy.length() - 1);

			statement = conn.prepareCall(String.format("{call dbo.%s(%s)}",
					message.getSpName(), occupy.toString()));
		}

		Collections.sort(params);
		int currentParameterIndex = 1;
		for (int i = 0; i < params.size(); i++) {
			params.get(i).setParameterIndex(currentParameterIndex);
			params.get(i).setPreparedStatement(statement);
			currentParameterIndex = params.get(i).getParameterIndex() + 1;
		}

		return statement;
	}

	private void executeQuery(Response resp, PreparedStatement statement)
			throws SQLException {
		resp.setResultType(ResultTypeEnum.RETRIEVE);

		ResultSet rs = statement.executeQuery();
		resp.setResultSet(getFromResultSet(rs));
	}

	private void executeUpdate(Response resp, PreparedStatement statement)
			throws SQLException {
		resp.setResultType(ResultTypeEnum.CUD);

		int rowCount = statement.executeUpdate();
		resp.setAffectRowCount(rowCount);
	}

	private void executeBatch(Response resp, PreparedStatement statement)
			throws SQLException {
		resp.setResultType(ResultTypeEnum.CUD);

		int[] rowCounts = statement.executeBatch();
		int rowCount = 0;
		for (int r : rowCounts) {
			rowCount += r;
		}
		resp.setAffectRowCount(rowCount);
	}

	/**
	 * TODO Shall we simply use getObject to allow best match for data type?
	 * TODO Shall we send data back even before iterate the whole result set
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private List<List<Parameter>> getFromResultSet(ResultSet rs)
			throws SQLException {

		ResultSetMetaData metaData = rs.getMetaData();

		int totalColumns = metaData.getColumnCount();

		int[] colTypes = new int[totalColumns];

		for (int i = 1; i <= totalColumns; i++) {
			int currentColType = metaData.getColumnType(i);
			colTypes[i - 1] = currentColType;
		}

		List<List<Parameter>> results = new ArrayList<List<Parameter>>();

		// Convert ResultSet object to a list of Parameter
		while (rs.next()) {
			List<Parameter> result = new ArrayList<Parameter>();
			for (int i = 1; i <= totalColumns; i++) {
				switch (colTypes[i - 1]) {
				case java.sql.Types.BOOLEAN:
					result.add(ParameterFactory.createBooleanParameter(i,
							rs.getBoolean(i)));
					break;
				case java.sql.Types.TINYINT:
					result.add(ParameterFactory.createByteParameter(i,
							rs.getByte(i)));
					break;
				case java.sql.Types.SMALLINT:
					result.add(ParameterFactory.createShortParameter(i,
							rs.getShort(i)));
					break;
				case java.sql.Types.INTEGER:
					result.add(ParameterFactory.createIntParameter(i,
							rs.getInt(i)));
					break;
				case java.sql.Types.BIGINT:
					result.add(ParameterFactory.createLongParameter(i,
							rs.getLong(i)));
					break;
				case java.sql.Types.FLOAT:
					result.add(ParameterFactory.createFloatParameter(i,
							rs.getFloat(i)));
					break;
				case java.sql.Types.DOUBLE:
					result.add(ParameterFactory.createDoubleParameter(i,
							rs.getDouble(i)));
					break;
				case java.sql.Types.DECIMAL:
					result.add(ParameterFactory.createDecimalParameter(i,
							rs.getBigDecimal(i)));
					break;
				case java.sql.Types.VARCHAR:
				case java.sql.Types.NVARCHAR:
				case java.sql.Types.LONGVARCHAR:
				case java.sql.Types.LONGNVARCHAR:
					result.add(ParameterFactory.createStringParameter(i,
							rs.getString(i)));
					break;
				case java.sql.Types.DATE:
					Date tempDate = rs.getDate(i);
					result.add(ParameterFactory.createTimestampParameter(i,
							new Timestamp(tempDate.getTime())));
					break;
				case java.sql.Types.TIME:
					Time tempTime = rs.getTime(i);
					result.add(ParameterFactory.createTimestampParameter(i,
							new Timestamp(tempTime.getTime())));
					break;
				case java.sql.Types.TIMESTAMP:
					result.add(ParameterFactory.createTimestampParameter(i,
							rs.getTimestamp(i)));
					break;
				case java.sql.Types.BINARY:
				case java.sql.Types.BLOB:
				case java.sql.Types.LONGVARBINARY:
				case java.sql.Types.VARBINARY:
					result.add(ParameterFactory.createByteArrayParameter(i,
							rs.getBytes(i)));
					break;
				default:
					break;
				}
			}
			results.add(result);
		}

		return results;
	}

	private void cleanUp(Response resp, Connection conn, Statement statement,
			long start) {
		// TODO should add a field to response to indicate exceptional case
		// happens
		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
				logger.error(CLOSE_CONNECTION_EXCEPTION, e);
			}
		}

		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				logger.error(CLOSE_STATEMENT_EXCEPTION, e);
			}
		}

		logger.warn(DURATION
				+ String.valueOf(System.currentTimeMillis() - start));
	}
}
