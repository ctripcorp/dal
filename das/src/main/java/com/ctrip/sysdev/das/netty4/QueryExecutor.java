package com.ctrip.sysdev.das.netty4;

import io.netty.channel.ChannelHandlerContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.msgpack.type.Value;
import org.msgpack.type.ValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.sysdev.das.DruidDataSourceWrapper;
import com.ctrip.sysdev.das.domain.Request;
import com.ctrip.sysdev.das.domain.RequestMessage;
import com.ctrip.sysdev.das.domain.Response;
import com.ctrip.sysdev.das.domain.StatementParameter;
import com.ctrip.sysdev.das.domain.enums.OperationType;
import com.ctrip.sysdev.das.domain.enums.StatementType;

public class QueryExecutor {
	public static final String QUERY_EXECUTION_EXCEPTION = "Query execution exception";
	public static final String CLOSE_CONNECTION_EXCEPTION = "Connection close exception";
	public static final String CLOSE_STATEMENT_EXCEPTION = "Statement close exception";
	public static final String DURATION = "duration";

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private DruidDataSourceWrapper dataSource;
	private ResponseSerializer responseSerializer = new ResponseSerializer();

	public QueryExecutor(DruidDataSourceWrapper dataSource) {
		this.dataSource = dataSource;
	}

	public void execute(Request request, ChannelHandlerContext ctx) {
		responseSerializer.writeResponseHeader(ctx, request);

		Response resp = new Response(request);
		Connection conn = null;
		PreparedStatement statement = null;
		RequestMessage message = request.getMessage();

		addDelay();
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
					executeQuery(ctx, resp, statement);
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
					executeUpdate(ctx, resp, statement);
					// }
					// conn.commit();
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
			resp.dbEnd();
			logger.error(QUERY_EXECUTION_EXCEPTION, e);
		} finally {
			cleanUp(resp, conn, statement, start);
		}
	}

	private boolean debug = false;

	private void addDelay() {
		if (!debug)
			return;

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
			statement = conn.prepareStatement(message.getSql(),
					ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
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

	private void executeQuery(ChannelHandlerContext ctx, Response resp,
			PreparedStatement statement) throws Exception {
		resp.setResultType(OperationType.Read);

		ResultSet rs = statement.executeQuery();
		resp.dbEnd();

		// Mark start encoding
		resp.encodeStart();
		getFromResultSet(ctx, rs, resp);
		// resp.setResultSet(null);
		resp.encodeEnd();
	}

	private void executeUpdate(ChannelHandlerContext ctx, Response resp,
			PreparedStatement statement) throws Exception {
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
	private void getFromResultSet(ChannelHandlerContext ctx, ResultSet rs,
			Response resp) throws Exception {
		rs.setFetchSize(20000);
		ResultSetMetaData metaData = rs.getMetaData();

		int totalColumns = metaData.getColumnCount();
		int[] columnTypes = new int[totalColumns];
		for (int i = 0; i < totalColumns; i++) {
			columnTypes[i] = metaData.getColumnType(i + 1);
		}
		
		responseSerializer.writeResultSetHeader(ctx, rs);

		List<Value[]> rows = new ArrayList<Value[]>();

		int bucket = getBucketCount(rs, 2);
		
		

		int rowCount = 0;
		int totalCount = 0;
		while (rs.next()) {
			Value[] row = new Value[totalColumns];

			for (int i = 0; i < totalColumns; i++) {
				row[i] = getColumnValue(rs, i, columnTypes[i]);
			}
			rows.add(row);
			// check for chunk
			totalCount++;
			rowCount++;
			if (rowCount == bucket) {
				responseSerializer.write(ctx, rows, null);
				rows = new ArrayList<Value[]>();
				rowCount = 0;
			}
		}
		resp.totalCount = totalCount;
		responseSerializer.write(ctx, rows, resp);
	}

	private int getBucketCount(ResultSet rs, int hint) throws Exception {
		checkRsLoopTime(rs, false);
		if(hint != 0)
			return hint;
		
		rs.last();
		int count = rs.getRow();
		rs.beforeFirst();

		int bucket = 300;
		if (count > 20000)
			bucket = 3000;
		return bucket;
	}

	private void checkRsLoopTime(ResultSet rs, boolean tt) throws SQLException {
		if(!tt)
			return;
		long t = System.currentTimeMillis();
		while(rs.next());
		logger.info("RS loop in ms: " + (System.currentTimeMillis() - t));
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
	}

	private Value getColumnValue(ResultSet rs, int index, int type) throws Exception {
		int i = index + 1;

		switch (type) {
		case java.sql.Types.BOOLEAN:
			return ValueFactory.createBooleanValue(rs.getBoolean(i));
		case java.sql.Types.TINYINT:
			return ValueFactory.createIntegerValue(rs.getByte(i));
		case java.sql.Types.SMALLINT:
			return ValueFactory.createIntegerValue(rs.getShort(i));
		case java.sql.Types.INTEGER:
			return ValueFactory.createIntegerValue(rs.getInt(i));
		case java.sql.Types.BIGINT:
			return ValueFactory.createIntegerValue(rs.getLong(i));
		case java.sql.Types.FLOAT:
			return ValueFactory.createFloatValue(rs.getFloat(i));
		case java.sql.Types.DOUBLE:
			return ValueFactory.createFloatValue(rs.getDouble(i));
		case java.sql.Types.DECIMAL:
			return ValueFactory.createRawValue(rs.getBigDecimal(i).toString());
		case java.sql.Types.VARCHAR:
		case java.sql.Types.NVARCHAR:
		case java.sql.Types.LONGVARCHAR:
		case java.sql.Types.LONGNVARCHAR:
			return ValueFactory.createRawValue(rs.getString(i));
		case java.sql.Types.DATE:
			return getDateTypeValue(rs.getDate(i));
		case java.sql.Types.TIME:
			return getDateTypeValue(rs.getTime(i));
		case java.sql.Types.TIMESTAMP:
			return getDateTypeValue(rs.getTimestamp(i));
		case java.sql.Types.BINARY:
		case java.sql.Types.BLOB:
		case java.sql.Types.LONGVARBINARY:
		case java.sql.Types.VARBINARY:
			return ValueFactory.createRawValue(rs.getBytes(i));
		default:
			break;
		}

		return ValueFactory.createNilValue();
	}
	
	private Value getDateTypeValue(Date tempDate) {
		return tempDate == null ? 
				ValueFactory.createNilValue() :
				ValueFactory.createIntegerValue(tempDate.getTime());
	}
}
