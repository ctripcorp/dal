package com.dal.sqlserver.test.control;

import java.sql.SQLException;
import java.util.List;

import com.ctrip.platform.dal.dao.DalHints;
import com.dal.sqlserver.test.People;
import com.dal.sqlserver.test.PeopleDao;
import com.xrosstools.xunit.Context;
import com.xrosstools.xunit.Processor;

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
				value = dao.queryAll(hints);
				break;
			case "queryByPage":
				int pageSize = context.getInt("pageSize"); 
				int pageNo = context.getInt("pageNo");
				value  = dao.queryAllByPage(pageSize, pageNo, hints);
				break;
			case "count":
				value = dao.count(hints);
				break;
			case "deleteAll":
				List<People> pList = dao.queryAll(hints);
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
