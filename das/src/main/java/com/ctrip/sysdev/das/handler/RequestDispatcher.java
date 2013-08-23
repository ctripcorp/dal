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

import com.ctrip.sysdev.das.enums.ActionTypeEnum;
import com.ctrip.sysdev.das.enums.MessageTypeEnum;
import com.ctrip.sysdev.das.enums.ResultTypeEnum;
import com.ctrip.sysdev.das.msg.AvailableType;
import com.ctrip.sysdev.das.msg.Message;
import com.ctrip.sysdev.das.response.DefaultResponse;

public class RequestDispatcher {

	private SQLRequestHandler sqlHandler;

	private SPRequestHandler spHandler;

	public DefaultResponse dispatch(Message message) throws Exception {
		
		DefaultResponse result = new DefaultResponse();
		
		// If the message type is sql, we call SQLHandler
		if (message.getMessageType() == MessageTypeEnum.SQL) {

			if (sqlHandler == null) {
				sqlHandler = new SQLRequestHandler();
				if (spHandler == null) {
					sqlHandler.init();
				}
			}

			// We call fetch if the action is retrieve
			if (message.getActionType() == ActionTypeEnum.SELECT) {
				ResultSet rs = sqlHandler.fetch(null, message.getSql(),
						message.getFlags(), message.getArgs().get(0));
				result.setResultType(ResultTypeEnum.RETRIEVE);

				result.setResultSet(getFromResultSet(rs));

			} else {
				int rowCount = sqlHandler.execute(null, message.getSql(),
						message.getFlags(), message.getArgs());
				result.setResultType(ResultTypeEnum.CUD);

				result.setAffectRowCount(rowCount);
			}

		} else {

			if (spHandler == null) {
				spHandler = new SPRequestHandler();
				if (sqlHandler == null) {
					spHandler.init();
				}
			}

			// We call fetch if the action is retrieve
			if (message.getActionType() == ActionTypeEnum.SELECT) {
				ResultSet rs = spHandler.fetchBySp(null, message.getSpName(),
						message.getFlags(), message.getArgs().get(0));
				result.setResultType(ResultTypeEnum.RETRIEVE);

				result.setResultSet(getFromResultSet(rs));

			} else {
				int rowCount = spHandler.executeSp(null, message.getSpName(),
						message.getFlags(), message.getArgs().get(0));
				result.setResultType(ResultTypeEnum.CUD);

				result.setAffectRowCount(rowCount);
			}

		}

		return result;

	}

	private List<List<AvailableType>> getFromResultSet(ResultSet rs)
			throws SQLException {

		ResultSetMetaData metaData = rs.getMetaData();

		int totalColumns = metaData.getColumnCount();

		int[] colTypes = new int[totalColumns];

		for (int i = 1; i <= totalColumns; i++) {
			int currentColType = metaData.getColumnType(i);
			colTypes[i - 1] = currentColType;
		}

		List<List<AvailableType>> results = new ArrayList<List<AvailableType>>();

		// Convert ResultSet object to a list of AvailableType
		while (rs.next()) {
			List<AvailableType> result = new ArrayList<AvailableType>();
			for (int i = 1; i <= totalColumns; i++) {
				switch (colTypes[i - 1]) {
				case java.sql.Types.BOOLEAN:
					result.add(new<Boolean> AvailableType(i, rs.getBoolean(i)));
					break;
				case java.sql.Types.TINYINT:
					result.add(new<Byte> AvailableType(i, rs.getByte(i)));
					break;
				case java.sql.Types.SMALLINT:
					result.add(new<Short> AvailableType(i, rs.getShort(i)));
					break;
				case java.sql.Types.INTEGER:
					result.add(new<Integer> AvailableType(i, rs.getInt(i)));
					break;
				case java.sql.Types.BIGINT:
					result.add(new<Long> AvailableType(i, rs.getLong(i)));
					break;
				case java.sql.Types.FLOAT:
					result.add(new<Float> AvailableType(i, rs.getFloat(i)));
					break;
				case java.sql.Types.DOUBLE:
					result.add(new<Double> AvailableType(i, rs.getDouble(i)));
					break;
				case java.sql.Types.DECIMAL:
					result.add(new<BigDecimal> AvailableType(i, rs
							.getBigDecimal(i)));
					break;
				case java.sql.Types.VARCHAR:
				case java.sql.Types.NVARCHAR:
				case java.sql.Types.LONGVARCHAR:
				case java.sql.Types.LONGNVARCHAR:
					result.add(new<String> AvailableType(i, rs.getString(i)));
					break;
				case java.sql.Types.DATE:
					Date tempDate = rs.getDate(i);
					result.add(new<Timestamp> AvailableType(i, new Timestamp(
							tempDate.getTime())));
					break;
				case java.sql.Types.TIME:
					Time tempTime = rs.getTime(i);
					result.add(new<Timestamp> AvailableType(i, new Timestamp(
							tempTime.getTime())));
					break;
				case java.sql.Types.TIMESTAMP:
					result.add(new<Timestamp> AvailableType(i, rs
							.getTimestamp(i)));
					break;
				case java.sql.Types.BINARY:
				case java.sql.Types.BLOB:
				case java.sql.Types.LONGVARBINARY:
				case java.sql.Types.VARBINARY:
					result.add(new<byte[]> AvailableType(i, rs.getBytes(i)));
					break;
				default:
					result.add(new AvailableType(i, rs.getObject(i)));
					break;
				}
			}
			results.add(result);
		}

		return results;
	}

}
