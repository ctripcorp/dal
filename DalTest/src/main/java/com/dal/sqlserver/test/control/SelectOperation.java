package com.dal.sqlserver.test.control;

import java.sql.SQLException;
import java.util.List;

import com.ctrip.platform.dal.dao.DalHints;
import com.dal.sqlserver.test.People;
import com.dal.sqlserver.test.PeopleDao;
import com.xross.tools.xunit.Context;
import com.xross.tools.xunit.Processor;

public class SelectOperation implements Processor {

	@Override
	public void process(Context ctx) {
		WebContext context = (WebContext)ctx;
		DalHints hints = context.getHints();
		PeopleDao dao = context.getDao();
		
		Object value = null;
		try {
			switch (context.getAction()) {
			case "queryByPk":
				value = dao.queryByPk(context.readPeople(), hints);
				break;
			case "getAll":
				value = dao.getAll(hints);
				break;
			case "queryByPage":
				int pageSize = context.getInt("pageSize"); 
				int pageNo = context.getInt("pageNo");
				value  = dao.queryByPage(pageSize, pageNo, hints);
				break;
			case "count":
				value = dao.count(hints);
				break;
			case "deleteAll":
				List<People> pList = dao.getAll(hints);
				value = dao.delete(hints, pList);
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
