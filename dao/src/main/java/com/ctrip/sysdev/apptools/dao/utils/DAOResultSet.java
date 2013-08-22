package com.ctrip.sysdev.apptools.dao.utils;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.ctrip.sysdev.apptools.dao.enums.AvailableTypeEnum;
import com.ctrip.sysdev.apptools.dao.msg.AvailableType;

public class DAOResultSet implements ResultSet {
	
	public DAOResultSet(List<List<AvailableType>> resultSet){
		this.resultSet = resultSet;
	}

	private List<List<AvailableType>> resultSet;

	private List<AvailableType> current;
	
	private Map<String, Integer> lableIndexMap;

	private int cursor = 0;

	public List<List<AvailableType>> getResultSet() {
		return this.resultSet;
	}

	public void setResultSet(List<List<AvailableType>> resultSet) {
		this.resultSet = resultSet;
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean next() throws SQLException {

		boolean result = cursor < resultSet.size();

		if (result) {
			current = resultSet.get(cursor);
		} else {
			current = null;
		}

		return result;

	}

	public void close() throws SQLException {
		resultSet.clear();
	}

	public boolean wasNull() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public String getString(int columnIndex) throws SQLException {
		AvailableType column = current.get(columnIndex-1);

		if (column.currentType != AvailableTypeEnum.STRING) {
			throw new SQLException("This column is not of type STRING!");
		}
		cursor++;
		return column.string_arg;
	}

	public boolean getBoolean(int columnIndex) throws SQLException {
		AvailableType column = current.get(columnIndex-1);

		if (column.currentType != AvailableTypeEnum.BOOL) {
			throw new SQLException("This column is not of type BOOL!");
		}
		cursor++;
		return column.bool_arg;
	}

	public byte getByte(int columnIndex) throws SQLException {
		AvailableType column = current.get(columnIndex-1);

		if (column.currentType != AvailableTypeEnum.BYTE) {
			throw new SQLException("This column is not of type BYTE!");
		}
		cursor++;
		return column.byte_arg;
	}

	public short getShort(int columnIndex) throws SQLException {
		AvailableType column = current.get(columnIndex-1);

		if (column.currentType != AvailableTypeEnum.SHORT) {
			throw new SQLException("This column is not of type SHORT!");
		}
		cursor++;
		return column.short_arg;
	}

	public int getInt(int columnIndex) throws SQLException {
		AvailableType column = current.get(columnIndex-1);

		if (column.currentType != AvailableTypeEnum.INT) {
			throw new SQLException("This column is not of type INT!");
		}
		cursor++;
		return column.int_arg;
	}

	public long getLong(int columnIndex) throws SQLException {
		AvailableType column = current.get(columnIndex-1);

		if (column.currentType != AvailableTypeEnum.LONG) {
			throw new SQLException("This column is not of type LONG!");
		}
		cursor++;
		return column.long_arg;
	}

	public float getFloat(int columnIndex) throws SQLException {
		AvailableType column = current.get(columnIndex-1);

		if (column.currentType != AvailableTypeEnum.FLOAT) {
			throw new SQLException("This column is not of type FLOAT!");
		}
		cursor++;
		return column.float_arg;
	}

	public double getDouble(int columnIndex) throws SQLException {
		AvailableType column = current.get(columnIndex-1);

		if (column.currentType != AvailableTypeEnum.DOUBLE) {
			throw new SQLException("This column is not of type DOUBLE!");
		}
		cursor++;
		return column.double_arg;
	}

	public BigDecimal getBigDecimal(int columnIndex, int scale)
			throws SQLException {
		AvailableType column = current.get(columnIndex-1);

		if (column.currentType != AvailableTypeEnum.DECIMAL) {
			throw new SQLException("This column is not of type DECIMAL!");
		}
		cursor++;
		return column.decimal_arg;
	}

	public byte[] getBytes(int columnIndex) throws SQLException {
		AvailableType column = current.get(columnIndex-1);

		if (column.currentType != AvailableTypeEnum.BYTEARR) {
			throw new SQLException("This column is not of type BYTE ARRAY!");
		}
		cursor++;
		return column.bytearr_arg;
	}

	public Date getDate(int columnIndex) throws SQLException {
		AvailableType column = current.get(columnIndex-1);

		if (column.currentType != AvailableTypeEnum.DATETIME) {
			throw new SQLException("This column is not of type DATETIME!");
		}
		cursor++;
		return new Date(column.datetime_arg.getTime());
	}

	public Time getTime(int columnIndex) throws SQLException {
		AvailableType column = current.get(columnIndex-1);

		if (column.currentType != AvailableTypeEnum.DATETIME) {
			throw new SQLException("This column is not of type DATETIME!");
		}
		cursor++;
		return new Time(column.datetime_arg.getTime());
	}

	public Timestamp getTimestamp(int columnIndex) throws SQLException {
		AvailableType column = current.get(columnIndex-1);

		if (column.currentType != AvailableTypeEnum.DATETIME) {
			throw new SQLException("This column is not of type DATETIME!");
		}
		cursor++;
		return column.datetime_arg;
	}

	public InputStream getAsciiStream(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public InputStream getUnicodeStream(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public InputStream getBinaryStream(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getString(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean getBoolean(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public byte getByte(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public short getShort(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getInt(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getLong(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public float getFloat(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getDouble(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public BigDecimal getBigDecimal(String columnLabel, int scale)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public byte[] getBytes(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Date getDate(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Time getTime(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Timestamp getTimestamp(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public InputStream getAsciiStream(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public InputStream getUnicodeStream(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public InputStream getBinaryStream(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public SQLWarning getWarnings() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public void clearWarnings() throws SQLException {
		// TODO Auto-generated method stub

	}

	public String getCursorName() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public ResultSetMetaData getMetaData() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getObject(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getObject(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public int findColumn(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public Reader getCharacterStream(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Reader getCharacterStream(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isBeforeFirst() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isAfterLast() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isFirst() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isLast() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public void beforeFirst() throws SQLException {
		// TODO Auto-generated method stub

	}

	public void afterLast() throws SQLException {
		// TODO Auto-generated method stub

	}

	public boolean first() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean last() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public int getRow() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean absolute(int row) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean relative(int rows) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean previous() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public void setFetchDirection(int direction) throws SQLException {
		// TODO Auto-generated method stub

	}

	public int getFetchDirection() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setFetchSize(int rows) throws SQLException {
		// TODO Auto-generated method stub

	}

	public int getFetchSize() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getType() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getConcurrency() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean rowUpdated() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean rowInserted() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean rowDeleted() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public void updateNull(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateBoolean(int columnIndex, boolean x) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateByte(int columnIndex, byte x) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateShort(int columnIndex, short x) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateInt(int columnIndex, int x) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateLong(int columnIndex, long x) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateFloat(int columnIndex, float x) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateDouble(int columnIndex, double x) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateBigDecimal(int columnIndex, BigDecimal x)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateString(int columnIndex, String x) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateBytes(int columnIndex, byte[] x) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateDate(int columnIndex, Date x) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateTime(int columnIndex, Time x) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateTimestamp(int columnIndex, Timestamp x)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateAsciiStream(int columnIndex, InputStream x, int length)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateBinaryStream(int columnIndex, InputStream x, int length)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateCharacterStream(int columnIndex, Reader x, int length)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateObject(int columnIndex, Object x, int scaleOrLength)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateObject(int columnIndex, Object x) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateNull(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateBoolean(String columnLabel, boolean x)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateByte(String columnLabel, byte x) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateShort(String columnLabel, short x) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateInt(String columnLabel, int x) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateLong(String columnLabel, long x) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateFloat(String columnLabel, float x) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateDouble(String columnLabel, double x) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateBigDecimal(String columnLabel, BigDecimal x)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateString(String columnLabel, String x) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateBytes(String columnLabel, byte[] x) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateDate(String columnLabel, Date x) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateTime(String columnLabel, Time x) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateTimestamp(String columnLabel, Timestamp x)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateAsciiStream(String columnLabel, InputStream x, int length)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateBinaryStream(String columnLabel, InputStream x, int length)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateCharacterStream(String columnLabel, Reader reader,
			int length) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateObject(String columnLabel, Object x, int scaleOrLength)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateObject(String columnLabel, Object x) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void insertRow() throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateRow() throws SQLException {
		// TODO Auto-generated method stub

	}

	public void deleteRow() throws SQLException {
		// TODO Auto-generated method stub

	}

	public void refreshRow() throws SQLException {
		// TODO Auto-generated method stub

	}

	public void cancelRowUpdates() throws SQLException {
		// TODO Auto-generated method stub

	}

	public void moveToInsertRow() throws SQLException {
		// TODO Auto-generated method stub

	}

	public void moveToCurrentRow() throws SQLException {
		// TODO Auto-generated method stub

	}

	public Statement getStatement() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getObject(int columnIndex, Map<String, Class<?>> map)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Ref getRef(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Blob getBlob(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Clob getClob(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Array getArray(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getObject(String columnLabel, Map<String, Class<?>> map)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Ref getRef(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Blob getBlob(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Clob getClob(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Array getArray(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Date getDate(int columnIndex, Calendar cal) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Date getDate(String columnLabel, Calendar cal) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Time getTime(int columnIndex, Calendar cal) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Time getTime(String columnLabel, Calendar cal) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Timestamp getTimestamp(int columnIndex, Calendar cal)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Timestamp getTimestamp(String columnLabel, Calendar cal)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public URL getURL(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public URL getURL(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public void updateRef(int columnIndex, Ref x) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateRef(String columnLabel, Ref x) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateBlob(int columnIndex, Blob x) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateBlob(String columnLabel, Blob x) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateClob(int columnIndex, Clob x) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateClob(String columnLabel, Clob x) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateArray(int columnIndex, Array x) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateArray(String columnLabel, Array x) throws SQLException {
		// TODO Auto-generated method stub

	}

	public RowId getRowId(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public RowId getRowId(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public void updateRowId(int columnIndex, RowId x) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateRowId(String columnLabel, RowId x) throws SQLException {
		// TODO Auto-generated method stub

	}

	public int getHoldability() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isClosed() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public void updateNString(int columnIndex, String nString)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateNString(String columnLabel, String nString)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateNClob(String columnLabel, NClob nClob)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public NClob getNClob(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public NClob getNClob(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public SQLXML getSQLXML(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public SQLXML getSQLXML(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public void updateSQLXML(int columnIndex, SQLXML xmlObject)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateSQLXML(String columnLabel, SQLXML xmlObject)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public String getNString(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getNString(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Reader getNCharacterStream(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Reader getNCharacterStream(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public void updateNCharacterStream(int columnIndex, Reader x, long length)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateNCharacterStream(String columnLabel, Reader reader,
			long length) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateAsciiStream(int columnIndex, InputStream x, long length)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateBinaryStream(int columnIndex, InputStream x, long length)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateCharacterStream(int columnIndex, Reader x, long length)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateAsciiStream(String columnLabel, InputStream x, long length)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateBinaryStream(String columnLabel, InputStream x,
			long length) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateCharacterStream(String columnLabel, Reader reader,
			long length) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateBlob(int columnIndex, InputStream inputStream, long length)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateBlob(String columnLabel, InputStream inputStream,
			long length) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateClob(int columnIndex, Reader reader, long length)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateClob(String columnLabel, Reader reader, long length)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateNClob(int columnIndex, Reader reader, long length)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateNClob(String columnLabel, Reader reader, long length)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateNCharacterStream(int columnIndex, Reader x)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateNCharacterStream(String columnLabel, Reader reader)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateAsciiStream(int columnIndex, InputStream x)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateBinaryStream(int columnIndex, InputStream x)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateCharacterStream(int columnIndex, Reader x)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateAsciiStream(String columnLabel, InputStream x)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateBinaryStream(String columnLabel, InputStream x)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateCharacterStream(String columnLabel, Reader reader)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateBlob(int columnIndex, InputStream inputStream)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateBlob(String columnLabel, InputStream inputStream)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateClob(int columnIndex, Reader reader) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateClob(String columnLabel, Reader reader)
			throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateNClob(int columnIndex, Reader reader) throws SQLException {
		// TODO Auto-generated method stub

	}

	public void updateNClob(String columnLabel, Reader reader)
			throws SQLException {
		// TODO Auto-generated method stub

	}

}
