package com.ctrip.platform.dal.tester.tasks;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.StatementParameters;

public class TaskTestStub {
	private String dbName;
	private DalTableDao<ClientTestModel> dao;
	private DalClient client;
	
	public TaskTestStub(String dbName) {
		this.dbName = dbName;
		dao = new DalTableDao<ClientTestModel>(new ClientTestDalParser(dbName));
		client = DalClientFactory.getClient(dbName);
	}

	public String getDbName() {
		return dbName;
	}

	public DalParser<ClientTestModel> getParser() {
		return new ClientTestDalParser(dbName);
	}
	
	public DalTableDao<ClientTestModel> getDao() {
		return dao;
	}
	
	public DalClient getClient() {
		return client;
	}
	
	public int getCount() throws SQLException {
		return getAllMap().size();
	}
	
	public List<ClientTestModel> getAll() throws SQLException {
		return dao.query("1=1", new StatementParameters(), new DalHints());
	}
	
	public Map<Integer, Map<String, ?>> getAllMap() throws SQLException {
		return dao.getPojosFieldsMap(dao.query("1=1", new StatementParameters(), new DalHints()));
	}
}
