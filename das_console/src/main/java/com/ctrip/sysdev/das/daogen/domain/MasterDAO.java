package com.ctrip.sysdev.das.daogen.domain;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.ctrip.platform.dao.AbstractDAO;
import com.ctrip.platform.dao.enums.DbType;
import com.ctrip.platform.dao.enums.ParameterDirection;
import com.ctrip.platform.dao.param.StatementParameter;

public class MasterDAO extends AbstractDAO {

	public MasterDAO() {
		logicDbName = "master";
		servicePort = 9000;
		credentialId = "30303";
		super.init();
	}

	public ResultSet getAllDbNames() {
		return this.fetch("use master select * from sysdatabases", null, null);
	}

	public ResultSet getPrimaryKey(String dbName, String tableName) {
		String sql = "use " + dbName + " SELECT Col.Column_Name from "
				+ " INFORMATION_SCHEMA.TABLE_CONSTRAINTS AS Tab "
				+ " , INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE AS Col "
				+ " WHERE Col.Constraint_Name = Tab.Constraint_Name "
				+ " AND Col.Table_Name = Tab.Table_Name "
				+ " AND Constraint_Type = 'PRIMARY KEY ' "
				+ " AND Col.Table_Name = '" + tableName + "'";

		return this.fetch(sql, null, null);
	}

	public ResultSet getAllColumns(String dbName, String tableName) {
		String sql = "use "
				+ dbName
				+ " SELECT COLUMN_NAME,DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS where TABLE_NAME =  '"
				+ tableName + "'";

		return this.fetch(sql, null, null);
	}

	public ResultSet getSPParams(String dbName,String schema, String spName) {
		String sql = "use "
				+ dbName
				+ " select PARAMETER_NAME, DATA_TYPE, PARAMETER_MODE,ORDINAL_POSITION from information_schema.parameters where specific_name='"
				+ spName + "' AND specific_schema='"+schema+"'";
		
		return this.fetch(sql, null, null);
	}

}
