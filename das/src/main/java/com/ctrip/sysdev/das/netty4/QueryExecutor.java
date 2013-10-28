package com.ctrip.sysdev.das.netty4;

import io.netty.channel.ChannelHandlerContext;

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
import java.util.List;

import org.msgpack.type.Value;
import org.msgpack.type.ValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.sysdev.das.DruidDataSourceWrapper;
import com.ctrip.sysdev.das.domain.RequestMessage;
import com.ctrip.sysdev.das.domain.Response;
import com.ctrip.sysdev.das.domain.StatementParameter;
import com.ctrip.sysdev.das.domain.enums.DbType;
import com.ctrip.sysdev.das.domain.enums.OperationType;
import com.ctrip.sysdev.das.domain.enums.StatementType;

public class QueryExecutor {
	public static final String QUERY_EXECUTION_EXCEPTION = "Query execution exception";
	public static final String CLOSE_CONNECTION_EXCEPTION = "Connection close exception";
	public static final String CLOSE_STATEMENT_EXCEPTION = "Statement close exception";
	public static final String DURATION = "duration";

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private ResponseSerializer responseSerializer = new ResponseSerializer();

	private DruidDataSourceWrapper dataSource;
	private RequestMessage message;
	private ChannelHandlerContext ctx;
	public QueryExecutor(DruidDataSourceWrapper dataSource, RequestMessage message, ChannelHandlerContext ctx) {
		this.dataSource = dataSource;
		this.message = message;
		this.ctx = ctx;
	}
	
	public Response execute() {
		Response resp = ctx.channel().attr(Response.RESPONSE_KEY).get();
		Connection conn = null;
		PreparedStatement statement = null;


//		addDelay();
		
		long start = System.currentTimeMillis();
		try {
			conn = dataSource.getConnection(message.getDbName());
			// conn.setAutoCommit(false);

			statement = createStatement(conn, message);

			resp.dbStart();
			if (message.getStatementType() == StatementType.StoredProcedure) {
				executeSP(resp, statement);
			} else {
				if (message.getOperationType() == OperationType.Read) {
					executeQuery(resp, statement);
				} else {
					// boolean batchOperation = false;
					// for (Parameter p : message.getArgs()) {
					// if (p.getParameterType() == ParameterType.PARAMARRAY) {
					// batchOperation = true;
					// break;
					// }
					// }
					// if (batchOperation) {
					// executeBatch(resp, statement);
					// } else {
					executeUpdate(resp, statement);
					// }
					// conn.commit();
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
			logger.error(QUERY_EXECUTION_EXCEPTION, e);
		} finally {
			cleanUp(resp, conn, statement, start);
		}

		return resp;
	}

	private void addDelay() {
		int i = 3;
		synchronized (this) {
			while (i-- > 0) {
				try {
					wait(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private PreparedStatement createStatement(Connection conn,
			RequestMessage message) throws Exception {
		// TODO: add batch operation
		List<StatementParameter> params = message.getArgs();

		PreparedStatement statement = null;

		if (message.getStatementType() == StatementType.SQL) {
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

		// Collections.sort(params);

		for (int i = 0; i < params.size(); i++) {
			params.get(i).setPreparedStatement(statement);
		}

		return statement;
	}

	private void executeQuery(Response resp, PreparedStatement statement)
			throws Exception {
		resp.setResultType(OperationType.Read);

		ResultSet rs = statement.executeQuery();
		resp.dbEnd();

		// Mark start encoding
		resp.encodeStart();
		getFromResultSet(rs, resp);
//		resp.setResultSet(null);
		resp.encodeEnd();
	}

	private void executeUpdate(Response resp, PreparedStatement statement)
			throws Exception {
		resp.setResultType(OperationType.Write);

		int rowCount = 0;
		// try{
		rowCount = statement.executeUpdate();
		resp.dbEnd();

		// Mark start encoding
		// ResultSet rs = statement.executeQuery();
		// CallableStatement cst = (CallableStatement) statement;
		// cst.getMoreResults();
		// System.out.println(((CallableStatement) statement).getInt(1));

		// statement.execute();
		// CallableStatement cst = (CallableStatement) statement;
		// ResultSet rs = cst.getResultSet();
		// while (rs.next()) {
		// System.out.println("**");
		// }
		// }catch(SQLException ex){
		//
		// }
		resp.setAffectRowCount(rowCount);
		resp.encodeStart();
		responseSerializer.writeRowCount(ctx, resp);
		resp.encodeEnd();
	}

	private void executeSP(Response resp, PreparedStatement statement)
			throws SQLException {
		resp.setResultType(OperationType.Write);

		int rowCount = 0;
		// try{
		// rowCount = statement.executeUpdate();
		statement.executeQuery();
		resp.dbEnd();

		// CallableStatement cst = (CallableStatement) statement;
		// cst.getMoreResults();
		// System.out.println(((CallableStatement) statement).getInt(1));

		// statement.execute();
		// CallableStatement cst = (CallableStatement) statement;
		// ResultSet rs = cst.getResultSet();
		// while (rs.next()) {
		// System.out.println("**");
		// }
		// }catch(SQLException ex){
		//
		// }
		resp.encodeStart();
		resp.setAffectRowCount(rowCount);
		resp.encodeEnd();
	}

	private void executeBatch(Response resp, PreparedStatement statement)
			throws SQLException {
		resp.setResultType(OperationType.Write);

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
	private void getFromResultSet(ResultSet rs, Response resp)
			throws Exception {

		ResultSetMetaData metaData = rs.getMetaData();

		int totalColumns = metaData.getColumnCount();

		int[] colTypes = new int[totalColumns];
		String[] colNames = new String[totalColumns];

		for (int i = 1; i <= totalColumns; i++) {
			int currentColType = metaData.getColumnType(i);
			colTypes[i - 1] = currentColType;
			colNames[i - 1] = metaData.getColumnLabel(i);
		}

		List<List<StatementParameter>> results = new ArrayList<List<StatementParameter>>();
		final int bucket = 2;
		int rowCount = 0;
		while (rs.next()) {
			List<StatementParameter> result = new ArrayList<StatementParameter>();
			for (int i = 1; i <= totalColumns; i++) {
				Value v;
				switch (colTypes[i - 1]) {
				case java.sql.Types.BOOLEAN:
					result.add(StatementParameter.createFromValue(i,
							colNames[i - 1], DbType.Boolean,
							ValueFactory.createBooleanValue(rs.getBoolean(i))));
					break;
				case java.sql.Types.TINYINT:
					result.add(StatementParameter.createFromValue(i,
							colNames[i - 1], DbType.Byte,
							ValueFactory.createIntegerValue(rs.getByte(i))));
					break;
				case java.sql.Types.SMALLINT:
					result.add(StatementParameter.createFromValue(i,
							colNames[i - 1], DbType.Int16,
							ValueFactory.createIntegerValue(rs.getShort(i))));
					break;
				case java.sql.Types.INTEGER:
					result.add(StatementParameter.createFromValue(i,
							colNames[i - 1], DbType.Int32,
							ValueFactory.createIntegerValue(rs.getInt(i))));
					break;
				case java.sql.Types.BIGINT:
					result.add(StatementParameter.createFromValue(i,
							colNames[i - 1], DbType.Int64,
							ValueFactory.createIntegerValue(rs.getLong(i))));
					break;
				case java.sql.Types.FLOAT:
					result.add(StatementParameter.createFromValue(i,
							colNames[i - 1], DbType.Single,
							ValueFactory.createFloatValue(rs.getFloat(i))));
					break;
				case java.sql.Types.DOUBLE:
					result.add(StatementParameter.createFromValue(i,
							colNames[i - 1], DbType.Double,
							ValueFactory.createFloatValue(rs.getDouble(i))));
					break;
				case java.sql.Types.DECIMAL:
					result.add(StatementParameter.createFromValue(i,
							colNames[i - 1], DbType.Double, 
							ValueFactory.createRawValue(rs.getBigDecimal(i).toString())));
					break;
				case java.sql.Types.VARCHAR:
				case java.sql.Types.NVARCHAR:
				case java.sql.Types.LONGVARCHAR:
				case java.sql.Types.LONGNVARCHAR:
					result.add(StatementParameter.createFromValue(i,
							colNames[i - 1], DbType.String,
							ValueFactory.createRawValue(rs.getString(i))));
					break;
				case java.sql.Types.DATE:
					Date tempDate = rs.getDate(i);
					v = tempDate == null ? ValueFactory.createNilValue()
							: ValueFactory.createIntegerValue(tempDate
									.getTime());
					result.add(StatementParameter.createFromValue(i,
							colNames[i - 1], DbType.DateTime, v));
					break;
				case java.sql.Types.TIME:
					Time tempTime = rs.getTime(i);
					v = tempTime == null ? ValueFactory.createNilValue()
							: ValueFactory.createIntegerValue(tempTime
									.getTime());
					result.add(StatementParameter.createFromValue(i,
							colNames[i - 1], DbType.DateTime, v));
					break;
				case java.sql.Types.TIMESTAMP:
					Timestamp tempTimestamp = rs.getTimestamp(i);
					v = tempTimestamp == null ? ValueFactory.createNilValue()
							: ValueFactory.createIntegerValue(tempTimestamp
									.getTime());
					result.add(StatementParameter.createFromValue(i,
							colNames[i - 1], DbType.DateTime, v));
					break;
				case java.sql.Types.BINARY:
				case java.sql.Types.BLOB:
				case java.sql.Types.LONGVARBINARY:
				case java.sql.Types.VARBINARY:
					result.add(StatementParameter.createFromValue(i,
							colNames[i - 1], DbType.Binary,
							ValueFactory.createRawValue(rs.getBytes(i))));
					break;
				default:
					break;
				}
			}
			results.add(result);
			// check for chunk
			rowCount++;
			if(rowCount == bucket) {
				responseSerializer.write(ctx, results, false, resp);
				results.clear();
				rowCount = 0;
			}
		}
		responseSerializer.write(ctx, results, true, resp);
		results.clear();
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
				+ String.valueOf(resp.getDbTime()));
	}
}
