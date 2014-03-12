package ${host.getPackageName()};

#foreach( $field in ${host.getDaoImports()} )
import ${field};
#end

#if($host.isSpa())
import com.ctrip.platform.dal.common.enums.ParameterDirection;
#end
import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.DalTableDao;
import com.ctrip.platform.dal.dao.StatementParameters;
#if($host.isSpa())
import com.ctrip.platform.dal.dao.helper.DalScalarExtractor;
#end

public class ${host.getPojoClassName()}Dao {
	private static final String DATA_BASE = "${host.getDbName()}";
#if($host.isSpa())

#if($host.getSpaInsert().isExist())
	private static final String INSERT_SPA_NAME = "${host.getSpaInsert().getMethodName()}";
#end
#if($host.getSpaDelete().isExist())
	private static final String DELETE_SPA_NAME = "${host.getSpaDelete().getMethodName()}";
#end
#if($host.getSpaUpdate().isExist())
	private static final String UPDATE_SPA_NAME = "${host.getSpaUpdate().getMethodName()}";
#end

	private static final String RET_CODE = "retcode";
	private static final String UPDATE_COUNT = "update_count";
	private DalScalarExtractor extractor = new DalScalarExtractor();

#end
	private DalParser<${host.getPojoClassName()}> parser = new ${host.getPojoClassName()}Parser();
	private DalTableDao<${host.getPojoClassName()}> client;
	private DalClient baseClient;

	public ${host.getPojoClassName()}Dao() {
		this.client = new DalTableDao<${host.getPojoClassName()}>(parser);
		this.baseClient = DalClientFactory.getClient(DATA_BASE);
	}

#if($host.isHasIdentity())
	public ${host.getPojoClassName()} queryByPk(Number id)
			throws SQLException {
		DalHints hints = new DalHints();
		return client.queryByPk(id, hints);
	}
#end

	public ${host.getPojoClassName()} queryByPk(${host.getPojoClassName()} pk)
			throws SQLException {
		DalHints hints = new DalHints();
		return client.queryByPk(pk, hints);
	}
	
	// TODO add query by PK column list
	
	public List<${host.getPojoClassName()}> queryByPage(${host.getPojoClassName()} pk, int pageSize, int pageNo)
			throws SQLException {
		// TODO to be implemented
		DalHints hints = new DalHints();
		return null;
	}

#if($host.getSpaInsert().isExist())
	public int insert(${host.getPojoClassName()} daoPojo) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		
		String callSql = prepareSpaCall(INSERT_SPA_NAME, parameters, parser.getFields(daoPojo));

#foreach($p in $host.getSpaInsert().getParameters())
#if($p.getDirection().name() == "InputOutput")
		parameters.registerInOut("${p.getName()}", ${p.getJavaTypeDisplay()}, daoPojo.get${p.getCapitalizedName()}());
#end
#if($p.getDirection().name() == "Output")
		parameters.registerOut("${p.getName()}", ${p.getJavaTypeDisplay()});
#end
#end

		Map<String, ?> results = baseClient.call(callSql, parameters, hints);

#foreach($p in $host.getSpaInsert().getParameters())
#if($p.getDirection().name() == "InputOutput")
		${p.getClassDisplayName()} ${p.getUncapitalizedName()} = (${p.getClassDisplayName()})parameters.get("${p.getName()}", ParameterDirection.InputOutput).getValue();
#end
#if($p.getDirection().name() == "Output")
		${p.getClassDisplayName()} ${p.getUncapitalizedName()} = (${p.getClassDisplayName()})parameters.get("${p.getName()}", ParameterDirection.Output).getValue();
#end
#end
		
		return (Integer)results.get(RET_CODE);
	}
#{else}
	public void insert(${host.getPojoClassName()}...daoPojos) throws SQLException {
		DalHints hints = new DalHints();
		client.insert(hints, null, daoPojos);
	}

	public void insert(KeyHolder keyHolder, ${host.getPojoClassName()}...daoPojos) throws SQLException {
		DalHints hints = new DalHints();
		client.insert(hints, keyHolder, daoPojos);
	}
#end

#if($host.getSpaDelete().isExist())	
	public int delete(${host.getPojoClassName()} daoPojo) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		
		String callSql = prepareSpaCall(DELETE_SPA_NAME, parameters, parser.getPrimaryKeys(daoPojo));
#foreach($p in $host.getSpaDelete().getParameters())
#if($p.getDirection().name() == "InputOutput")
		parameters.registerInOut("${p.getName()}", ${p.getJavaTypeDisplay()}, daoPojo.get${p.getCapitalizedName()}());
#end
#if($p.getDirection().name() == "Output")
		parameters.registerOut("${p.getName()}", ${p.getJavaTypeDisplay()});
#end
#end

		Map<String, ?> results = baseClient.call(callSql, parameters, hints);
#foreach($p in $host.getSpaDelete().getParameters())
#if($p.getDirection().name() == "InputOutput")
		${p.getClassDisplayName()} ${p.getUncapitalizedName()} = (${p.getClassDisplayName()})parameters.get("${p.getName()}", ParameterDirection.InputOutput).getValue();
#end
#if($p.getDirection().name() == "Output")
		${p.getClassDisplayName()} ${p.getUncapitalizedName()} = (${p.getClassDisplayName()})parameters.get("${p.getName()}", ParameterDirection.Output).getValue();
#end
#end
		
		return (Integer)results.get(RET_CODE);
	}
#{else}
	public void delete(${host.getPojoClassName()}...daoPojos) throws SQLException {
		DalHints hints = new DalHints();
		client.delete(hints, daoPojos);
	}
#end

#if($host.getSpaUpdate().isExist())	
	public int update(${host.getPojoClassName()} daoPojo) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		
		String callSql = prepareSpaCall(UPDATE_SPA_NAME, parameters, parser.getFields(daoPojo));
#foreach($p in $host.getSpaUpdate().getParameters())
#if($p.getDirection().name() == "InputOutput")
		parameters.registerInOut("${p.getName()}", ${p.getJavaTypeDisplay()}, daoPojo.get${p.getCapitalizedName()}());
#end
#if($p.getDirection().name() == "Output")
		parameters.registerOut("${p.getName()}", ${p.getJavaTypeDisplay()});
#end
#end

		Map<String, ?> results = baseClient.call(callSql, parameters, hints);
#foreach($p in $host.getSpaUpdate().getParameters())
#if($p.getDirection().name() == "InputOutput")
		${p.getClassDisplayName()} ${p.getUncapitalizedName()} = (${p.getClassDisplayName()})parameters.get("${p.getName()}", ParameterDirection.InputOutput).getValue();
#end
#if($p.getDirection().name() == "Output")
		${p.getClassDisplayName()} ${p.getUncapitalizedName()} = (${p.getClassDisplayName()})parameters.get("${p.getName()}", ParameterDirection.Output).getValue();
#end
#end
		
		return (Integer)results.get(RET_CODE);
	}
#{else}
	public void update(${host.getPojoClassName()}...daoPojos) throws SQLException {
		DalHints hints = new DalHints();
		client.update(hints, daoPojos);
	}
#end

#foreach($method in $host.getMethods())
#if($method.getCrud_type() == "select")
    public List<${host.getPojoClassName()}> ${method.getName()}(${method.getParameterDeclaration()}) 
			throws SQLException {
#else
    public int ${method.getName()}(${method.getParameterDeclaration()}) throws SQLException {
#end
		String sql = "${method.getSql()}";
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		int i = 1;
#foreach($p in $method.getParameters())  
		parameters.set(i++, ${p.getJavaTypeDisplay()}, ${p.getName()});
#end
#if($method.getCrud_type() == "select")
		return queryDao.query(sql, parameters, hints, personRowMapper);
#else
		return baseClient.update(sql, parameters, hint);
#end
	}
#end

#if($host.isSpa())
	private String prepareSpaCall(String spaName, StatementParameters parameters, Map<String, ?> fields) {
		client.addParametersByName(parameters, fields);
		String callSql = client.buildCallSql(spaName, fields.size());
		parameters.setResultsParameter(UPDATE_COUNT);
		parameters.setResultsParameter(RET_CODE, extractor);
		return callSql;
	}
	
#end
	private static class ${host.getPojoClassName()}Parser implements DalParser<${host.getPojoClassName()}> {
		public static final String DATABASE_NAME = "${host.getDbName()}";
		public static final String TABLE_NAME = "${host.getTableName()}";
		private static final String[] COLUMNS = new String[]{
#foreach( $field in ${host.getFields()} )
			"${field.getName()}",
#end
		};
		
		private static final String[] PRIMARY_KEYS = new String[]{
#foreach( $field in ${host.getFields()} )
#if($field.isPrimary())
			"${field.getName()}",
#end
#end
		};
		
		private static final int[] COLUMN_TYPES = new int[]{
#foreach( $field in ${host.getFields()} )
			${field.getJavaTypeDisplay()},
#end
		};
		
		@Override
		public ${host.getPojoClassName()} map(ResultSet rs, int rowNum) throws SQLException {
			${host.getPojoClassName()} pojo = new ${host.getPojoClassName()}();
			
#foreach( $field in ${host.getFields()} )
			pojo.set${field.getCapitalizedName()}((${field.getClassDisplayName()})rs.getObject("${field.getName()}"));
#end
	
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
			return ${host.isHasIdentity()};
		}
	
		@Override
		public Number getIdentityValue(${host.getPojoClassName()} pojo) {
			return #if($host.isHasIdentity())pojo.get${host.getCapitalizedIdentityColumnName()}()#{else}null#end;
		}
	
		@Override
		public Map<String, ?> getPrimaryKeys(${host.getPojoClassName()} pojo) {
			Map<String, Object> primaryKeys = new LinkedHashMap<String, Object>();
			
#foreach( $field in ${host.getFields()} )
#if($field.isPrimary())
			primaryKeys.put("${field.getName()}", pojo.get${field.getCapitalizedName()}());
#end
#end
	
			return primaryKeys;
		}
	
		@Override
		public Map<String, ?> getFields(${host.getPojoClassName()} pojo) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			
#foreach( $field in ${host.getFields()} )
			map.put("${field.getName()}", pojo.get${field.getCapitalizedName()}());
#end
	
			return map;
		}
	}
}