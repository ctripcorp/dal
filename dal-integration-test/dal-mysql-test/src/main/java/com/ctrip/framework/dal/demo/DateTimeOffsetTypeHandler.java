package com.ctrip.framework.dal.demo;

import com.microsoft.sqlserver.jdbc.SQLServerCallableStatement;
import com.microsoft.sqlserver.jdbc.SQLServerPreparedStatement;
import com.microsoft.sqlserver.jdbc.SQLServerResultSet;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import microsoft.sql.DateTimeOffset;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class DateTimeOffsetTypeHandler extends BaseTypeHandler<DateTimeOffset> {

  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, DateTimeOffset parameter, JdbcType jdbcType)
      throws SQLException {
    SQLServerPreparedStatement sqlServerPreparedStatement = ps.unwrap(SQLServerPreparedStatement.class);
    sqlServerPreparedStatement.setDateTimeOffset(i, parameter);
  }

  @Override
  public DateTimeOffset getNullableResult(ResultSet rs, String columnName) throws SQLException {
    return ((SQLServerResultSet)rs).getDateTimeOffset(columnName);
  }

  @Override
  public DateTimeOffset getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    return ((SQLServerResultSet)rs).getDateTimeOffset(columnIndex);
  }

  @Override
  public DateTimeOffset getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    return ((SQLServerCallableStatement)cs).getDateTimeOffset(columnIndex);
  }
}
