package com.dal.sqlserver.test.control;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ctrip.platform.dal.dao.DalHints;
import com.dal.sqlserver.test.People;
import com.dal.sqlserver.test.PeopleDao;
import com.xross.tools.xunit.Context;
import com.xross.tools.xunit.Processor;

public class SelectOperation implements Processor {

	@Override
	public void process(Context ctx) {
		WebContext context = (WebContext)ctx;
		DalHints hints = context.readHints();
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
				value = dao.batchUpdate(hints, context.readPeopleList());
				break;
			case "count":
				value = dao.count(hints);
				break;
			case "deleteAll":
				value = dao.getAll(hints);
				int pageSize = context.getInt("pageSize"); 
				int pageNo = context.getInt("pageNo");
				value  = dao.queryByPage(pageSize, pageNo, hints);
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
