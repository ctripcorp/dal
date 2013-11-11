package com.ctrip.sysdev.das.netty4;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.sysdev.das.DruidDataSourceWrapper;
import com.ctrip.sysdev.das.domain.DasProto;
import com.ctrip.sysdev.das.domain.enums.DbType;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

public class QueryExecutor {
	public static final String QUERY_EXECUTION_EXCEPTION = "Query execution exception";
	public static final String CLOSE_CONNECTION_EXCEPTION = "Connection close exception";
	public static final String CLOSE_STATEMENT_EXCEPTION = "Statement close exception";
	public static final String DURATION = "duration";
	
	private long dbStart = 0;
	private long encodeStart = 0;

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private DruidDataSourceWrapper dataSource;

	public QueryExecutor(DruidDataSourceWrapper dataSource) {
		this.dataSource = dataSource;
	}

	public void execute(DasProto.Request request, ChannelHandlerContext ctx) {

		Connection conn = null;
		PreparedStatement statement = null;

		DasProto.Response.Builder resp = DasProto.Response.newBuilder();

		addDelay();

		long start = System.currentTimeMillis();
		try {
			conn = getConnection(request);
			// conn.setAutoCommit(false);

			resp.setId(request.getId());
			
			dbStart = System.currentTimeMillis();
			
			statement = createStatement(conn, request);

			if (request.getMsg().getStateType() == DasProto.StatementType.SP) {
				executeSP(resp, statement);
			} else {
				if (request.getMsg().getCrud() == DasProto.CRUD.GET) {
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
			logger.error(QUERY_EXECUTION_EXCEPTION, e);
		} finally {
			cleanUp(resp, conn, statement, start);
		}
	}

	private Connection getConnection(DasProto.Request request)
			throws SQLException {
		if (request.getMsg().getMaster())
			return dataSource.getMasterConnection(request.getDb());

		if (request.getMsg().getCrud() == DasProto.CRUD.GET)
			return dataSource.getSlaveConnection(request.getDb());

		return dataSource.getMasterConnection(request.getDb());
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
			DasProto.Request request) throws Exception {
		// TODO: add batch operation
		List<DasProto.SqlParameters> params = request.getMsg()
				.getParametersList();

		PreparedStatement statement = null;

		if (request.getMsg().getStateType() == DasProto.StatementType.SQL) {
			statement = conn.prepareStatement(request.getMsg().getName(),
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		} else {
			StringBuffer occupy = new StringBuffer();

			for (int i = 0; i < params.size(); i++) {
				occupy.append("?").append(",");
			}

			occupy.deleteCharAt(occupy.length() - 1);

			statement = conn.prepareCall(String.format("{call dbo.%s(%s)}",
					request.getMsg().getName(), occupy.toString()));
		}

		// Collections.sort(params);

		for (int i = 0; i < params.size(); i++) {
			 setSqlParameter(statement, params.get(i));
		}

		return statement;
	}

	private void executeQuery(ChannelHandlerContext ctx,
			DasProto.Response.Builder resp, PreparedStatement statement)
			throws Exception {

		resp.setResultType(DasProto.CRUD.GET);

		ResultSet rs = statement.executeQuery();
		
		TimeCostSendTask.getInstance().getQueue().add(
				String.format("id=%s&timeCost=dbTime:%d", resp.getId(), 
						System.currentTimeMillis() - dbStart));

		// Mark start encoding
		getFromResultSet(ctx, rs, resp);
		// resp.setResultSet(null);
	}

	private void executeUpdate(ChannelHandlerContext ctx,
			DasProto.Response.Builder resp, PreparedStatement statement)
			throws Exception {
		resp.setResultType(DasProto.CRUD.CUD);

		int rowCount = 0;
		// try{
		rowCount = statement.executeUpdate();

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
		resp.setAffectRows(rowCount);

		byte[] headerPayload = resp.build().toByteArray();

		ByteBuf buf = ctx.alloc().buffer();
		buf.writeInt(headerPayload.length + 2);

		// The version
		buf.writeInt(1);
		buf.writeBytes(headerPayload);
		ctx.writeAndFlush(buf);

	}

	private void executeSP(DasProto.Response.Builder resp,
			PreparedStatement statement) throws SQLException {

		int rowCount = 0;
		// try{
		// rowCount = statement.executeUpdate();
		statement.executeQuery();
		

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
		resp.setAffectRows(rowCount);

		// TODO: write to client

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
			DasProto.Response.Builder resp) throws Exception {
		rs.setFetchSize(20000);
		ResultSetMetaData metaData = rs.getMetaData();

		int totalColumns = metaData.getColumnCount();
		int[] columnTypes = new int[totalColumns];

		for (int i = 0; i < totalColumns; i++) {
			int currentType = metaData.getColumnType(i + 1);
			columnTypes[i] = currentType;
			DasProto.ResponseHeader.Builder headerBuilder = DasProto.ResponseHeader
					.newBuilder();
			resp.addHeader(
					i,
					headerBuilder.setType(currentType)
							.setName(metaData.getColumnLabel(i + 1)).build());
		}
		
		encodeStart = System.currentTimeMillis();

		// Serialize and write the header
		byte[] headerPayload = resp.build().toByteArray();

		ByteBuf buf = ctx.alloc().buffer();
		buf.writeInt(headerPayload.length + 2);
		buf.writeShort(1);
		buf.writeBytes(headerPayload);
		ctx.writeAndFlush(buf);
		
		//buf.clear();
		headerPayload = null;

		int bucket = 2;

		int rowCount = 0;
		int totalCount = 0;

		DasProto.InnerResultSet.Builder builder = DasProto.InnerResultSet
				.newBuilder();
		int flush = 10;
		while (rs.next()) {
			totalCount++;
			DasProto.Row.Builder rowBuilder = DasProto.Row.newBuilder();
			for (int i = 0; i < totalColumns; i++) {
				rowBuilder.addColumns(getColumnValue(rs, i, columnTypes[i]));
			}

			builder.addRows(rowBuilder.build());
			rowBuilder.clear();
			// check for chunk
			// totalCount++;
			rowCount++;
			if (rowCount == bucket) {
				builder.setLast(false);

				WriteResultSet(ctx, builder);

				builder.clear();
				builder = DasProto.InnerResultSet.newBuilder();
				rowCount = 0;
			}
			
			if(flush-- == 0){
				ctx.flush();
				flush = 30;
			}
		}

		builder.setLast(true);

		WriteResultSet(ctx, builder);
		ctx.flush();
		
		TimeCostSendTask.getInstance().getQueue().add(
				String.format("id=%s&timeCost=encodeResponseTime:%d", resp.getId(), 
						System.currentTimeMillis() - encodeStart));
		
		rs.close();
		
		if(totalCount > 100000){
			logger.info("calling GC");
			Runtime.getRuntime().gc();
		}
		logger.info("Finished");
	}

	private void WriteResultSet(ChannelHandlerContext ctx,
			DasProto.InnerResultSet.Builder builder) throws InvalidProtocolBufferException {
		byte[] bodyPayload = builder.build().toByteArray();
		
		ByteBuf bf = ctx.alloc().buffer();

		bf.writeInt(bodyPayload.length);
		bf.writeBytes(bodyPayload);
		ChannelFuture wf = ctx.write(bf);
		//bf.clear();
		bodyPayload = null;
	}

	private void cleanUp(DasProto.Response.Builder resp, Connection conn,
			Statement statement, long start) {
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

	private DasProto.AvailableType getColumnValue(ResultSet rs, int index,
			int type) throws Exception {
		int i = index + 1;

		switch (type) {
		case java.sql.Types.BOOLEAN:
			boolean booleanValue = rs.getBoolean(i);
			if(rs.wasNull())
				break;
			return DasProto.AvailableType.newBuilder().setCurrent(0)
					.setBoolArg(booleanValue).build();
		case java.sql.Types.TINYINT:
			byte byteValue = rs.getByte(i);
			if(rs.wasNull())
				break;
			return DasProto.AvailableType.newBuilder().setCurrent(1)
					.setInt32Arg(byteValue).build();
		case java.sql.Types.SMALLINT:
			short shortValue = rs.getShort(i);
			if(rs.wasNull())
				break;
			return DasProto.AvailableType.newBuilder().setCurrent(1)
					.setInt32Arg(shortValue).build();
		case java.sql.Types.INTEGER:
			int intValue = rs.getInt(i);
			if(rs.wasNull())
				break;
			return DasProto.AvailableType.newBuilder().setCurrent(1)
					.setInt32Arg(intValue).build();
		case java.sql.Types.BIGINT:
			long longValue = rs.getLong(i);
			if(rs.wasNull())
				break;
			return DasProto.AvailableType.newBuilder().setCurrent(2)
					.setInt64Arg(longValue).build();
		case java.sql.Types.FLOAT:
			float floatValue = rs.getFloat(i);
			if(rs.wasNull())
				break;
			return DasProto.AvailableType.newBuilder().setCurrent(3)
					.setDoubleArg(floatValue).build();
		case java.sql.Types.DOUBLE:
			double doubleValue = rs.getDouble(i);
			if(rs.wasNull())
				break;
			return DasProto.AvailableType.newBuilder().setCurrent(3)
					.setDoubleArg(doubleValue).build();
		case java.sql.Types.DECIMAL:
			BigDecimal decimalValue = rs.getBigDecimal(i);
			if(rs.wasNull())
				break;
			return DasProto.AvailableType.newBuilder().setCurrent(4)
					.setStringArg(decimalValue.toString()).build();
		case java.sql.Types.VARCHAR:
		case java.sql.Types.NVARCHAR:
		case java.sql.Types.LONGVARCHAR:
		case java.sql.Types.LONGNVARCHAR:
			String stringValue = rs.getString(i);
			if(rs.wasNull())
				break;
			return DasProto.AvailableType.newBuilder().setCurrent(4)
					.setStringArg(stringValue).build();
		case java.sql.Types.DATE:
			Date dateValue = rs.getDate(i);
			if(rs.wasNull())
				break;
			return DasProto.AvailableType.newBuilder().setCurrent(2)
					.setInt64Arg(dateValue.getTime()).build();
		case java.sql.Types.TIME:
			Time timeValue = rs.getTime(i);
			if(rs.wasNull())
				break;
			return DasProto.AvailableType.newBuilder().setCurrent(2)
					.setInt64Arg(timeValue.getTime()).build();
		case java.sql.Types.TIMESTAMP:
			Timestamp timestampValue = rs.getTimestamp(i);
			if(rs.wasNull())
				break;
			return DasProto.AvailableType.newBuilder().setCurrent(2)
					.setInt64Arg(timestampValue.getTime()).build();
		case java.sql.Types.BINARY:
		case java.sql.Types.BLOB:
		case java.sql.Types.LONGVARBINARY:
		case java.sql.Types.VARBINARY:
			byte[] bytesValue = rs.getBytes(i);
			if(rs.wasNull())
				break;
			return DasProto.AvailableType.newBuilder().setCurrent(5)
					.setBytesArg(ByteString.copyFrom(bytesValue)).build();
		default:
			break;
		}

		return DasProto.AvailableType.newBuilder().setCurrent(-1).build();
	}

	private PreparedStatement setSqlParameter(PreparedStatement ps,
			DasProto.SqlParameters parameter) throws SQLException {
		DbType type = DbType.fromInt(parameter.getDbType());

		switch (type) {
		case Boolean:
			ps.setBoolean(parameter.getIndex(), parameter.getValue()
					.getBoolArg());
			break;
		case Binary:
			ps.setBytes(parameter.getIndex(), parameter.getValue()
					.getBytesArg().toByteArray());
			break;
		case Byte:
			ps.setByte(parameter.getIndex(), (byte) parameter.getValue()
					.getInt32Arg());
			break;
		case DateTime:
			ps.setTimestamp(parameter.getIndex(), new Timestamp(parameter
					.getValue().getInt64Arg()));
			break;
		case Decimal:
			break;
		case Double:
			ps.setDouble(parameter.getIndex(), parameter.getValue().getDoubleArg());
			break;
		case Guid:
			break;
		case Int16:
			ps.setShort(parameter.getIndex(),  (short)parameter.getValue()
					.getInt32Arg());
			break;
		case Int32:
			ps.setInt(parameter.getIndex(),  parameter.getValue()
					.getInt32Arg());
			break;
		case Int64:
			ps.setLong(parameter.getIndex(),  parameter.getValue()
					.getInt64Arg());
			break;
		case SByte:
			break;
		case Single:
			ps.setFloat(parameter.getIndex(), (float)parameter.getValue().getDoubleArg());
			break;
		case String:
			ps.setString(parameter.getIndex(), parameter.getValue().getStringArg());
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
