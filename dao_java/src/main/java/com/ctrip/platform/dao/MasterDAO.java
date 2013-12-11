package com.ctrip.platform.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ctrip.platform.dao.enums.DbType;
import com.ctrip.platform.dao.enums.ParameterDirection;
import com.ctrip.platform.dao.param.StatementParameter;

public class MasterDAO extends AbstractDAO {

	public MasterDAO() {
		logicDbName = "SysDalTest";
		servicePort = 9000;
		credentialId = "30303";
		super.init();
	}

	public ResultSet fetchBySp(String name) {
		List<StatementParameter> parameters = new ArrayList<StatementParameter>();

		parameters.add(StatementParameter.newBuilder().setDbType(DbType.String)
				.setDirection(ParameterDirection.Input).setNullable(false)
				.setIndex(1).setName("").setSensitive(false).setValue(name)
				.build());
		return this.fetchBySp("sp_helpindex", parameters, null);
	}

	public static void main(String[] args) throws SQLException {
		int count = 0;
		ResultSet rs = new MasterDAO().fetchBySp("Person");
		if (null != rs) {
			while (rs.next()) {
				count++;
				System.out.println(rs.getString(1));
			}
			rs.close();
		}
		System.out.println(count);
	}

}
