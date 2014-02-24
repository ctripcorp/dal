package com.ctrip.platform.dal.tester.person;

import java.sql.SQLException;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalCommand;

public class DalPersonCommand implements DalCommand {

	@Override
	public boolean execute(DalClient client) throws SQLException {
//		Result = client.query(sql, parameters, hints, visitor);
//		// TODO Auto-generated method stub
//		
//		if(result == null)
//			return false;
//		
//		client.update(sql, parameters, hints);
		return true;
	}

}
