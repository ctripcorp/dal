package com.ctrip.sysdev.das.netty4;

import io.netty.channel.ChannelHandlerContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
		if(!debug)
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
					ResultSet.TYPE_SCROLL_INSENSITIVE,
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

	private void executeQuery(ChannelHandlerContext ctx, Response resp, PreparedStatement statement)
			throws Exception {
		resp.setResultType(OperationType.Read);

		ResultSet rs = statement.executeQuery();
		resp.dbEnd();

		// Mark start encoding
		resp.encodeStart();
		getFromResultSet(ctx, rs, resp);
//		resp.setResultSet(null);
		resp.encodeEnd();
	}

	private void executeUpdate(ChannelHandlerContext ctx, Response resp, PreparedStatement statement)
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
	private void getFromResultSet(ChannelHandlerContext ctx,  ResultSet rs, Response resp)
			throws Exception {
		rs.last();
		int count = rs.getRow();
		rs.beforeFirst();
		
		ResultSetMetaData metaData = rs.getMetaData();

		int totalColumns = metaData.getColumnCount();

		List<byte[][]> rows = new ArrayList<byte[][]>();

		int bucket = getBucketCount(count);

		int rowCount = 0;
		int totalCount = 0;
		while (rs.next()) {
			byte[][] row = new byte[totalColumns][];
			
			for (int i = 0; i < totalColumns; i++) {
				row[i] = rs.getBytes(i + 1);
			}
			rows.add(row);
			// check for chunk
			totalCount++;
			rowCount++;
			if(rowCount == bucket) {
				responseSerializer.write(ctx, rows, null);
				rows = new ArrayList<byte[][]>();
				rowCount = 0;
			}
		}
		resp.totalCount = totalCount;
		responseSerializer.write(ctx, rows, resp);
	}

	private int getBucketCount(int count) {
		int bucket = 300;
		if(count > 20000)
			bucket = 2;
		return bucket;
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
}
