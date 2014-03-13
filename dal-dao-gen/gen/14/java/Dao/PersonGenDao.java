package DAL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ctrip.platform.dal.common.enums.ParameterDirection;
import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DalScalarExtractor;

public class PersonGenDao {
	private static final String DATA_BASE = "PerformanceTest";

	private static final String INSERT_SPA_NAME = "spA_Person_i";
	private static final String DELETE_SPA_NAME = "spA_Person_d";
	private static final String UPDATE_SPA_NAME = "spA_Person_u";

	private static final String RET_CODE = "retcode";
	private static final String UPDATE_COUNT = "update_count";
	private DalScalarExtractor extractor = new DalScalarExtractor();

	private DalParser<PersonGen> parser = new PersonGenParser();
	private DalTableDao<PersonGen> client;
	private DalClient baseClient;

	public PersonGenDao() {
		this.client = new DalTableDao<PersonGen>(parser);
		this.baseClient = DalClientFactory.getClient(DATA_BASE);
	}

	public PersonGen queryByPk(Number id)
			throws SQLException {
		DalHints hints = new DalHints();
		return client.queryByPk(id, hints);
	}

	public PersonGen queryByPk(PersonGen pk)
			throws SQLException {
		DalHints hints = new DalHints();
		return client.queryByPk(pk, hints);
	}
	
	// TODO add query by PK column list
	
	public List<PersonGen> queryByPage(PersonGen pk, int pageSize, int pageNo)
			throws SQLException {
		// TODO to be implemented
		DalHints hints = new DalHints();
		return null;
	}

	public int insert(PersonGen daoPojo) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		
		String callSql = prepareSpaCall(INSERT_SPA_NAME, parameters, parser.getFields(daoPojo));

		parameters.registerInOut("@ID", Types.INTEGER, daoPojo.getID());

		Map<String, ?> results = baseClient.call(callSql, parameters, hints);

		Integer iD = (Integer)parameters.get("@ID", ParameterDirection.InputOutput).getValue();
		
		return (Integer)results.get(RET_CODE);
	}

	public int delete(PersonGen daoPojo) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		
		String callSql = prepareSpaCall(DELETE_SPA_NAME, parameters, parser.getPrimaryKeys(daoPojo));

		Map<String, ?> results = baseClient.call(callSql, parameters, hints);
		
		return (Integer)results.get(RET_CODE);
	}

	public int update(PersonGen daoPojo) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		
		String callSql = prepareSpaCall(UPDATE_SPA_NAME, parameters, parser.getFields(daoPojo));
		parameters.registerInOut("@ID", Types.INTEGER, daoPojo.getID());

		Map<String, ?> results = baseClient.call(callSql, parameters, hints);
		Integer iD = (Integer)parameters.get("@ID", ParameterDirection.InputOutput).getValue();
		
		return (Integer)results.get(RET_CODE);
	}

    public List<PersonGen> GetNameByID(Integer ID) 
			throws SQLException {
		String sql = "SELECT Name FROM Person WHERE  ID = @ID ";
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		int i = 1;
		parameters.set(i++, Types.INTEGER, ID);
		return queryDao.query(sql, parameters, hints, personRowMapper);
	}
    public int deleteByName(String Name) throws SQLException {
		String sql = "Delete FROM Person WHERE  Name = @Name ";
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		int i = 1;
		parameters.set(i++, Types.VARCHAR, Name);
		return baseClient.update(sql, parameters, hint);
	}

	private String prepareSpaCall(String spaName, StatementParameters parameters, Map<String, ?> fields) {
		client.addParametersByName(parameters, fields);
		String callSql = client.buildCallSql(spaName, fields.size());
		parameters.setResultsParameter(UPDATE_COUNT);
		parameters.setResultsParameter(RET_CODE, extractor);
		return callSql;
	}
	
	private static class PersonGenParser implements DalParser<PersonGen> {
		public static final String DATABASE_NAME = "PerformanceTest";
		public static final String TABLE_NAME = "Person";
		private static final String[] COLUMNS = new String[]{
			"ID",
			"Name",
			"Age",
			"Birth",
		};
		
		private static final String[] PRIMARY_KEYS = new String[]{
			"ID",
		};
		
		private static final int[] COLUMN_TYPES = new int[]{
			Types.INTEGER,
			Types.VARCHAR,
			Types.INTEGER,
			Types.TIMESTAMP,
		};
		
		@Override
		public PersonGen map(ResultSet rs, int rowNum) throws SQLException {
			PersonGen pojo = new PersonGen();
			
			pojo.setID((Integer)rs.getObject("ID"));
			pojo.setName((String)rs.getObject("Name"));
			pojo.setAge((Integer)rs.getObject("Age"));
			pojo.setBirth((Timestamp)rs.getObject("Birth"));
	
			return pojo;
		}
	
		@Override
		public String getDatabaseName() {
			return DATABASE_NAME;
		}
	
		@Override
		public String getTableName() {
			return TABLE_NAME;
		}
	
		@Override
		public String[] getColumnNames() {
			return COLUMNS;
		}
	
		@Override
		public String[] getPrimaryKeyNames() {
			return PRIMARY_KEYS;
		}
		
		@Override
		public int[] getColumnTypes() {
			return COLUMN_TYPES;
		}
	
		@Override
		public boolean isAutoIncrement() {
			return true;
		}
	
		@Override
		public Number getIdentityValue(PersonGen pojo) {
			return pojo.getID();
		}
	
		@Override
		public Map<String, ?> getPrimaryKeys(PersonGen pojo) {
			Map<String, Object> primaryKeys = new LinkedHashMap<String, Object>();
			
			primaryKeys.put("ID", pojo.getID());
	
			return primaryKeys;
		}
	
		@Override
		public Map<String, ?> getFields(PersonGen pojo) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			
			map.put("ID", pojo.getID());
			map.put("Name", pojo.getName());
			map.put("Age", pojo.getAge());
			map.put("Birth", pojo.getBirth());
	
			return map;
		}
	}
}