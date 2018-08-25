package test.com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.ctrip.platform.dal.dao.*;
import com.ctrip.platform.dal.dao.helper.DalDefaultJpaParser;

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

	public <T> DalParser<T> getParser(Class<T> modelClazz) throws SQLException {
		return new DalDefaultJpaParser<T>(modelClazz, dbName);
	}

	public DalParser<ClientTestModel> getParser() {
		return new ClientTestDalParser(dbName);
	}
	
	Map<String, ?> getFields(Object o) throws SQLException {
		return new DalDefaultJpaParser(o.getClass(), getDbName()).getFields(o);
	}
	
	public DalTableDao<ClientTestModel> getDao() {
		return dao;
	}
	
	public <T> DalTableDao<T> getDao(Class<T> clazz) throws SQLException {
		return new DalTableDao<T>(new DalDefaultJpaParser<>(clazz, getDbName()));
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
	
	public <T> List<T> getAll(Class<T> clazz) throws SQLException {
		return new DalTableDao<T>(new DalDefaultJpaParser<>(clazz, getDbName())).query("1=1", new StatementParameters(), new DalHints());
	}
	
	public Map<Integer, Map<String, ?>> getAllMap() throws SQLException {
		return dao.getPojosFieldsMap(dao.query("1=1", new StatementParameters(), new DalHints()));
	}
}
