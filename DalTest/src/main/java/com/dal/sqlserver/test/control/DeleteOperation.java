package com.dal.sqlserver.test.control;

import java.sql.SQLException;

import com.ctrip.platform.dal.dao.DalHints;
import com.dal.sqlserver.test.PeopleDao;
import com.xross.tools.xunit.Context;
import com.xross.tools.xunit.Processor;

public class DeleteOperation implements Processor {

	@Override
	public void process(Context ctx) {
		WebContext context = (WebContext)ctx;
		DalHints hints = context.readHints();
		PeopleDao dao = context.getDao();
		
		Object num = null;
		try {
			switch (context.getAction()) {
			case "deleteSingle":
				num = dao.delete(hints , context.readPeople());
				break;
			case "deleteMultiple":
				num = dao.delete(hints, context.readPeopleList());
				break;
			case "batchDelete":
				num = dao.batchDelete(hints, context.readPeopleList());
				break;
			default:
				break;
			}
		} catch (SQLException e) {
			context.handle(e);
		}
		
		context.setResponsValue(num);
	}
}
