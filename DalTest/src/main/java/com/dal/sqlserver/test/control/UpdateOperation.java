package com.dal.sqlserver.test.control;

import java.sql.SQLException;

import com.ctrip.platform.dal.dao.DalHints;
import com.dal.sqlserver.test.PeopleDao;
import com.xross.tools.xunit.Context;
import com.xross.tools.xunit.Processor;

public class UpdateOperation implements Processor {

	@Override
	public void process(Context ctx) {
		WebContext context = (WebContext)ctx;
		DalHints hints = context.readHints();
		PeopleDao dao = context.getDao();
		
		Object num = null;
		try {
			switch (context.getAction()) {
			case "updateSingle":
				num = dao.update(hints , context.readPeople());
				break;
			case "updateMultiple":
				num = dao.update(hints, context.readPeopleList());
				break;
			case "batchUpdate":
				num = dao.batchUpdate(hints, context.readPeopleList());
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
