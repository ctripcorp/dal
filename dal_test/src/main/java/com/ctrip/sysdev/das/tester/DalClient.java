package com.ctrip.sysdev.das.tester;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ctrip.platform.dao.AbstractDAO;
import com.ctrip.platform.dao.param.StatementParameter;


public class DalClient extends AbstractDAO {
	private List<StatementParameter> parameters = new ArrayList<StatementParameter>();
	private Map keywordParameters = new HashMap();
	
	public void init() {
		
	}
	
	public void executeQuery(String sql) {
		if(true)
			return;
		// read result set
		ResultSet rs = this.fetch(sql, parameters, keywordParameters);
		try {
			while(rs.next()){
				rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	public void executeUpdate(String sql) {
		this.execute(sql, parameters, keywordParameters);
	}
}
