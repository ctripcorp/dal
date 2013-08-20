package com.ctrip.sysdev.das.handler;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.ctrip.sysdev.das.enums.ActionType;
import com.ctrip.sysdev.das.enums.MessageType;
import com.ctrip.sysdev.das.enums.ResultType;
import com.ctrip.sysdev.das.msg.AvailableType;
import com.ctrip.sysdev.das.msg.MessageObject;
import com.ctrip.sysdev.das.msg.ResultObject;

public class MessageDispatcher {

	private SQLHandler sqlHandler;

	private SPHandler spHandler;

	public ResultObject dispatch(MessageObject message) throws Exception {
		
		ResultObject result = new ResultObject();
		
		// If the message type is sql, we call SQLHandler
		if (message.messageType == MessageType.SQL) {

			if (sqlHandler == null) {
				sqlHandler = new SQLHandler();
				if (spHandler == null) {
					sqlHandler.init();
				}
			}

			// We call fetch if the action is retrieve
			if (message.actionType == ActionType.SELECT) {
				ResultSet rs = sqlHandler.fetch(null, message.SQL,
						message.flags, message.singleArgs);
				result.resultType = ResultType.RETRIEVE;

				result.resultSet = getFromResultSet(rs);

			} else {
				int rowCount = sqlHandler.execute(null, message.SQL,
						message.flags, message.singleArgs);
				result.resultType = ResultType.CUD;

				result.affectRowCount = rowCount;
			}

		} else {

			if (spHandler == null) {
				spHandler = new SPHandler();
				if (sqlHandler == null) {
					spHandler.init();
				}
			}

			// We call fetch if the action is retrieve
			if (message.actionType == ActionType.SELECT) {
				ResultSet rs = spHandler.fetchBySp(null, message.SPName,
						message.flags, message.singleArgs);
				result.resultType = ResultType.RETRIEVE;

				result.resultSet = getFromResultSet(rs);

			} else {
				int rowCount = spHandler.executeSp(null, message.SPName,
						message.flags, message.singleArgs);
				result.resultType = ResultType.CUD;

				result.affectRowCount = rowCount;
			}

		}

		return result;

	}

	private List<AvailableType> getFromResultSet(ResultSet rs)
			throws SQLException {

		ResultSetMetaData metaData = rs.getMetaData();

		int totalColumns = metaData.getColumnCount();

		int[] colTypes = new int[totalColumns];

		for (int i = 1; i <= totalColumns; i++) {
			int currentColType = metaData.getColumnType(i);
			colTypes[i - 1] = currentColType;
		}

		List<AvailableType> results = new ArrayList<AvailableType>();

		// Convert ResultSet object to a list of AvailableType
		while (rs.next()) {
			for (int i = 1; i <= totalColumns; i++) {
				switch (colTypes[i - 1]) {
				case java.sql.Types.BOOLEAN:
					results.add(new<Boolean> AvailableType(i, rs.getBoolean(i)));
					break;
				case java.sql.Types.TINYINT:
					results.add(new<Byte> AvailableType(i, rs.getByte(i)));
					break;
				case java.sql.Types.SMALLINT:
					results.add(new<Short> AvailableType(i, rs.getShort(i)));
					break;
				case java.sql.Types.INTEGER:
					results.add(new<Integer> AvailableType(i, rs.getInt(i)));
					break;
				case java.sql.Types.BIGINT:
					results.add(new<Long> AvailableType(i, rs.getLong(i)));
					break;
				case java.sql.Types.FLOAT:
					results.add(new<Float> AvailableType(i, rs.getFloat(i)));
					break;
				case java.sql.Types.DOUBLE:
					results.add(new<Double> AvailableType(i, rs.getDouble(i)));
					break;
				case java.sql.Types.DECIMAL:
					results.add(new<BigDecimal> AvailableType(i, rs
							.getBigDecimal(i)));
					break;
				case java.sql.Types.VARCHAR:
				case java.sql.Types.NVARCHAR:
				case java.sql.Types.LONGVARCHAR:
				case java.sql.Types.LONGNVARCHAR:
					results.add(new<String> AvailableType(i, rs.getString(i)));
					break;
				case java.sql.Types.DATE:
					Date tempDate = rs.getDate(i);
					results.add(new<Timestamp> AvailableType(i, new Timestamp(
							tempDate.getTime())));
					break;
				case java.sql.Types.TIME:
					Time tempTime = rs.getTime(i);
					results.add(new<Timestamp> AvailableType(i, new Timestamp(
							tempTime.getTime())));
					break;
				case java.sql.Types.TIMESTAMP:
					results.add(new<Timestamp> AvailableType(i, rs
							.getTimestamp(i)));
					break;
				case java.sql.Types.BINARY:
				case java.sql.Types.BLOB:
				case java.sql.Types.LONGVARBINARY:
				case java.sql.Types.VARBINARY:
					results.add(new<byte[]> AvailableType(i, rs.getBytes(i)));
					break;
				default:
					results.add(new AvailableType(i, rs.getObject(i)));
					break;
				}
			}
		}

		return results;
	}

}
