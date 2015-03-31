package com.ctrip.platform.dal.daogen.host.csharp;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.ResultSetExtractor;

import com.ctrip.platform.dal.common.enums.DbType;
import com.ctrip.platform.dal.daogen.Consts;
import com.ctrip.platform.dal.daogen.host.AbstractParameterHost;
import com.ctrip.platform.dal.daogen.utils.DbUtils;

public class CsharpColumnNameResultSetExtractor implements ResultSetExtractor<List<AbstractParameterHost>> {

	private String allInOneName;
	private String tableName;
	
	public CsharpColumnNameResultSetExtractor(String allInOneName, String tableName) {
		super();
		this.allInOneName = allInOneName;
		this.tableName = tableName;
	}

	@Override
	public List<AbstractParameterHost> extractData(ResultSet rs) throws SQLException {
		List<AbstractParameterHost> allColumns = new ArrayList<AbstractParameterHost>();
		Map<String, String> columnComment;
		try {
			columnComment = DbUtils.getSqlserverColumnComment(allInOneName, tableName);
		} catch (Exception e) {
			throw new SQLException(e.getMessage(), e);
		}
		while (rs.next()) {
			CSharpParameterHost host = new CSharpParameterHost();
			String typeName = rs.getString("TYPE_NAME");
			int dataType = rs.getInt("DATA_TYPE");
			int length = rs.getInt("COLUMN_SIZE");
			
			//特殊处理
			host.setDbType(DbUtils.getDotNetDbType(typeName, dataType, length));
			//host.setName(CommonUtils.normalizeVariable(allColumnsRs.getString("COLUMN_NAME")));
			host.setName(rs.getString("COLUMN_NAME"));
			String remark = rs.getString("REMARKS");
			if(remark == null){
				String description = columnComment.get(rs.getString("COLUMN_NAME").toLowerCase());
				remark = description==null?"":description;
			}
			host.setComment(remark.replace("\n", " "));
			host.setType(DbType.getCSharpType(host.getDbType()));
			host.setIdentity(rs.getString("IS_AUTOINCREMENT").equalsIgnoreCase("YES"));
			host.setNullable(rs.getShort("NULLABLE") == DatabaseMetaData.columnNullable);
			host.setValueType(Consts.CSharpValueTypes.contains(host.getType()));
			// 仅获取String类型的长度
			if ("string".equalsIgnoreCase(host.getType()))
				host.setLength(length);

			allColumns.add(host);
		}
		return allColumns;
	}

}
