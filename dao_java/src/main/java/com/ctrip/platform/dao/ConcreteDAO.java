package com.ctrip.platform.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ctrip.platform.dao.enums.DbType;
import com.ctrip.platform.dao.enums.ParameterDirection;
import com.ctrip.platform.dao.param.StatementParameter;

public class ConcreteDAO extends AbstractDAO {

	public ConcreteDAO() {
		logicDbName = "SysDalTest";
		servicePort = 9000;
		credentialId = "30303";
		super.init();
	}

	public ResultSet fetchAll() {
		return this.fetch("select * from Person", null, null);
	}

	public int updateNameById(int id, String name) {
		List<StatementParameter> parameters = new ArrayList<StatementParameter>();
		
		parameters.add(StatementParameter.newBuilder()
				.setDbType(DbType.String)
				.setDirection(ParameterDirection.Input).
				setNullable(false)
				.setIndex(1)
				.setName("")
				.setSensitive(false)
				.setValue(name)
				.build());
		
		parameters.add(StatementParameter.newBuilder()
				.setDbType(DbType.Int32)
				.setDirection(ParameterDirection.Input).
				setNullable(false)
				.setIndex(2)
				.setName("")
				.setSensitive(false)
				.setValue(id)
				.build());
		
		return this.execute("update Person set name = ? where id = ?", parameters, null);
		
	}
	

	public static void main(String[] args) throws SQLException {
		int count = 0;
		count = new ConcreteDAO().updateNameById(37, "kevin&snow");
		ResultSet rs = new ConcreteDAO().fetchAll();
		if (null != rs) {
			while (rs.next()) {
				count++;
			}
			rs.close();
		}
		System.out.println(count);
	}

}
