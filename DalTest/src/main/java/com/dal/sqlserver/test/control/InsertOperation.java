package com.dal.sqlserver.test.control;

import java.sql.SQLException;

import com.ctrip.platform.dal.dao.DalHints;
import com.dal.sqlserver.test.PeopleDao;
import com.xrosstools.xunit.Context;
import com.xrosstools.xunit.Processor;

public class InsertOperation implements Processor {

	@Override
	public void process(Context ctx) {
		WebContext context = (WebContext)ctx;
		DalHints hints = context.getHints();
		PeopleDao dao = context.getDao();
		
		Object num = null;
		try {
			switch (context.getAction()) {
			case "insertSingle":
				num = dao.insert(hints, context.readPeople());
				break;
			case "insertMultiple":
				num = dao.insert(hints, context.readPeopleList());
				break;
			case "batchInsert":
				num = dao.batchInsert(hints, context.readPeopleList());
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
