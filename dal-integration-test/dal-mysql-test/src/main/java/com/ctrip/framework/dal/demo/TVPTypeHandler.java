package com.ctrip.framework.dal.demo;

import com.microsoft.sqlserver.jdbc.SQLServerCallableStatement;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Collection;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import com.ctrip.framework.dal.demo.entity.App;
import com.google.common.base.Strings;
import com.microsoft.sqlserver.jdbc.SQLServerDataTable;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class TVPTypeHandler extends BaseTypeHandler<Collection<App>> {

  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, Collection<App> parameter, JdbcType jdbcType)
      throws SQLException {
    SQLServerDataTable dataTable = new SQLServerDataTable();

    dataTable.addColumnMetadata("AppId", Types.VARCHAR);
    dataTable.addColumnMetadata("DataChange_CreatedBy", Types.VARCHAR);
    dataTable.addColumnMetadata("DataChange_CreatedTime", Types.TIMESTAMP);
    dataTable.addColumnMetadata("DataChange_LastModifiedBy", Types.VARCHAR);
    dataTable.addColumnMetadata("DataChange_LastTime", Types.TIMESTAMP);
    dataTable.addColumnMetadata("ID", Types.INTEGER);
    dataTable.addColumnMetadata("Name", Types.VARCHAR);

    int count=0;
    Timestamp currentDate = new Timestamp(System.currentTimeMillis());
    for (App app : parameter) {
      dataTable.addRow(
          app.getAppId(),
          Strings.nullToEmpty(app.getDatachangeCreatedby()),
          currentDate,
          Strings.nullToEmpty(app.getDatachangeCreatedby()),
          currentDate,
          count++,
          app.getName()
      );
    }

    SQLServerCallableStatement sqlsvrStatement = ps.unwrap(SQLServerCallableStatement.class);
    sqlsvrStatement.setStructured(i, "TVP_App", dataTable);
  }

  @Override
  public Collection<App> getNullableResult(ResultSet rs, String columnName) throws SQLException {
    return null;
  }

  @Override
  public Collection<App> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    return null;
  }

  @Override
  public Collection<App> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    return null;
  }
}
