package com.ctrip.platform.dal.daogen.java;

import java.util.List;

import com.ctrip.platform.dal.daogen.AbstractGenerator;
import com.ctrip.platform.dal.daogen.dao.DaoOfDbServer;
import com.ctrip.platform.dal.daogen.pojo.GenTask;
import com.ctrip.platform.dal.daogen.pojo.GenTaskByFreeSql;
import com.ctrip.platform.dal.daogen.pojo.GenTaskByTableViewSp;
import com.ctrip.platform.dal.daogen.utils.SpringBeanGetter;

public class JavaGenerator extends AbstractGenerator {

	private JavaGenerator() {

	}

	private static JavaGenerator instance = new JavaGenerator();
	private static DaoOfDbServer dbServerDao;

	static {
		dbServerDao = SpringBeanGetter.getDaoOfDbServer();
	}

	public static JavaGenerator getInstance() {
		return instance;
	}

	@Override
	public void generateByTableView(List<GenTaskByTableViewSp> tasks) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void generateByFreeSql(List<GenTaskByFreeSql> tasks) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void generateBySqlBuilder(List<GenTask> tasks) {
		// TODO Auto-generated method stub
		
	}
	
}