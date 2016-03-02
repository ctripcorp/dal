package com.dal.sqlserver.test.control;

import java.sql.SQLException;

import com.ctrip.platform.dal.dao.DalHints;
import com.dal.sqlserver.test.PeopleDao;
import com.xross.tools.xunit.Context;
import com.xross.tools.xunit.Processor;

public class UserDefinedOperation implements Processor {

	@Override
	public void process(Context ctx) {
		WebContext context = (WebContext)ctx;
		DalHints hints = context.getHints();
		PeopleDao dao = context.getDao();
		
		Object value = null;
		try {
			switch (context.getAction()) {
			case "findPeople":
				int pageSize = context.getInt("pageSize"); 
				int pageNo = context.getInt("pageNo");
				String name = context.get("name");
				value = dao.findPeople(name, pageNo, pageSize, hints);
				break;
			case "insertPeople":
			case "deletePeople":
			case "updatePeople":
				context.setSupported(false);
				break;
			default:
				break;
			}
		} catch (SQLException e) {
			context.handle(e);
		}
		
		context.setResponsValue(value);
	}
}
