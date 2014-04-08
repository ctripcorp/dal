package ${host.getPackageName()};

#foreach( $field in ${host.getDaoImports()} )
import ${field};
#end

public class ${host.getPojoClassName()}Dao {
	private static final String DATA_BASE = "${host.getDbName()}";
	private static final String COUNT_SQL_PATTERN = "SELECT count(1) from ${host.getTableName()}";
	private static final String ALL_SQL_PATTERN = "SELECT * FROM ${host.getTableName()}";
#if($host.getDatabaseCategory().name() == "MySql")
	private static final String PAGE_MYSQL_PATTERN = "SELECT * FROM ${host.getTableName()} WHERE LIMIT %s, %s";
#else
	private static final String PAGE_SQL_PATTERN = "WITH CTE AS (select *, row_number() over(order by ${host.getOverColumns()} desc ) as rownum" 
			+" from ${host.getTableName()} (nolock)) select * from CTE where rownum between %s and %s";
#end
#if($host.isSp())

#if($host.getSpInsert().isExist())
	private static final String INSERT_SP_NAME = "${host.getSpInsert().getMethodName()}";
#end
#if($host.getSpDelete().isExist())
	private static final String DELETE_SP_NAME = "${host.getSpDelete().getMethodName()}";
#end
#if($host.getSpUpdate().isExist())
	private static final String UPDATE_SP_NAME = "${host.getSpUpdate().getMethodName()}";
#end
	private static final String RET_CODE = "retcode";
	private static final String UPDATE_COUNT = "update_count";
#end
	
	private DalParser<${host.getPojoClassName()}> parser = new ${host.getPojoClassName()}Parser();
	private DalScalarExtractor extractor = new DalScalarExtractor();
	private DalRowMapperExtractor<${host.getPojoClassName()}> rowextractor = null;
	private DalTableDao<${host.getPojoClassName()}> client;
	private DalClient baseClient;

	public ${host.getPojoClassName()}Dao() {
		this.client = new DalTableDao<${host.getPojoClassName()}>(parser);
		this.rowextractor = new DalRowMapperExtractor<${host.getPojoClassName()}>(parser); 
		this.baseClient = DalClientFactory.getClient(DATA_BASE);
	}

#if($host.isIntegerPk())
	public ${host.getPojoClassName()} queryByPk(Number id)
			throws SQLException {
		DalHints hints = new DalHints();
		return client.queryByPk(id, hints);
	}
#else
	public ${host.getPojoClassName()} queryByPk(${host.getPkParameterDeclaration()})
			throws SQLException {
		DalHints hints = new DalHints();
		${host.getPojoClassName()} pk = new ${host.getPojoClassName()}();
			
#foreach( $field in ${host.getPrimaryKeys()} )
		pojo.set${field.getCapitalizedName()}(${field.getUncapitalizedName()});
#end

		return client.queryByPk(pk, hints);
	}
#end

	public ${host.getPojoClassName()} queryByPk(${host.getPojoClassName()} pk)
			throws SQLException {
		DalHints hints = new DalHints();
		return client.queryByPk(pk, hints);
	}
	
	public int count()  throws SQLException
	{
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		
		Object result = this.baseClient.query(COUNT_SQL_PATTERN, parameters, hints, extractor);
		return Integer.valueOf(((Number)result).intValue());
	}
	
	public List<${host.getPojoClassName()}> queryByPage(${host.getPojoClassName()} pk, int pageSize, int pageNo)  throws SQLException {
		if(pageNo < 1 || pageSize < 1) 
			throw new SQLException("Illigal pagesize or pageNo, pls check");
		
        StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		
		String sql = "";
#if($host.getDatabaseCategory().name() == "MySql" )
		sql = String.format(PAGE_MYSQL_PATTERN, (pageNo - 1) * pageSize, pageSize);
#else
		int fromRownum = (pageNo - 1) * pageSize + 1;
        int endRownum = pageSize * pageNo;
		sql = String.format(PAGE_SQL_PATTERN, fromRownum, endRownum);
#end
		return this.baseClient.query(sql, parameters, hints, rowextractor);
	}
	
	public List<${host.getPojoClassName()}> getAll() throws SQLException
	{
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		List<${host.getPojoClassName()}> result = null;
		result = this.baseClient.query(ALL_SQL_PATTERN, parameters, hints, rowextractor);
		return result;
	}

#if($host.getSpInsert().isExist())
	public int insert(${host.getPojoClassName()} daoPojo) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		
		String callSql = prepareSpCall(INSERT_SP_NAME, parameters, parser.getFields(daoPojo));

#foreach($p in $host.getSpInsert().getParameters())
#if($p.getDirection().name() == "InputOutput")
		parameters.registerInOut("${p.getName()}", ${p.getJavaTypeDisplay()}, daoPojo.get${p.getCapitalizedName()}());
#end
#if($p.getDirection().name() == "Output")
		parameters.registerOut("${p.getName()}", ${p.getJavaTypeDisplay()});
#end
#end

		Map<String, ?> results = baseClient.call(callSql, parameters, hints);

#foreach($p in $host.getSpInsert().getParameters())
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

#if($host.getSpDelete().isExist())	
	public int delete(${host.getPojoClassName()} daoPojo) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		
		String callSql = prepareSpCall(DELETE_SP_NAME, parameters, parser.getPrimaryKeys(daoPojo));

#foreach($p in $host.getSpDelete().getParameters())
#if($p.getDirection().name() == "InputOutput")
		parameters.registerInOut("${p.getName()}", ${p.getJavaTypeDisplay()}, daoPojo.get${p.getCapitalizedName()}());
#end
#if($p.getDirection().name() == "Output")
		parameters.registerOut("${p.getName()}", ${p.getJavaTypeDisplay()});
#end
#end

		Map<String, ?> results = baseClient.call(callSql, parameters, hints);
#foreach($p in $host.getSpDelete().getParameters())
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

#if($host.getSpUpdate().isExist())	
	public int update(${host.getPojoClassName()} daoPojo) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		
		String callSql = prepareSpCall(UPDATE_SP_NAME, parameters, parser.getFields(daoPojo));

#foreach($p in $host.getSpUpdate().getParameters())
#if($p.getDirection().name() == "InputOutput")
		parameters.registerInOut("${p.getName()}", ${p.getJavaTypeDisplay()}, daoPojo.get${p.getCapitalizedName()}());
#end
#if($p.getDirection().name() == "Output")
		parameters.registerOut("${p.getName()}", ${p.getJavaTypeDisplay()});
#end
#end

		Map<String, ?> results = baseClient.call(callSql, parameters, hints);
#foreach($p in $host.getSpUpdate().getParameters())
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
		parameters.set(i++, ${p.getJavaTypeDisplay()}, ${p.getAlias()});
#end
#if($method.getCrud_type() == "select")
		return baseClient.query(sql, parameters, hints, rowextractor);
#else
		return baseClient.update(sql, parameters, hints);
#end
	}
#end

#if($host.isSp())
	private String prepareSpCall(String SpName, StatementParameters parameters, Map<String, ?> fields) {
		client.addParametersByName(parameters, fields);
		String callSql = client.buildCallSql(SpName, fields.size());
#if($host.isSpa())
		parameters.setResultsParameter(RET_CODE, extractor);
#end
		parameters.setResultsParameter(UPDATE_COUNT);
		return callSql;
	}
	
#end
	private static class ${host.getPojoClassName()}Parser extends AbstractDalParser<${host.getPojoClassName()}> {
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
		
		public ${host.getPojoClassName()}Parser() {
			super(DATABASE_NAME, TABLE_NAME, COLUMNS, PRIMARY_KEYS, COLUMN_TYPES);
		}
		
		@Override
		public ${host.getPojoClassName()} map(ResultSet rs, int rowNum) throws SQLException {
			${host.getPojoClassName()} pojo = new ${host.getPojoClassName()}();
			
#foreach( $field in ${host.getFields()} )
			pojo.set${field.getCapitalizedName()}((${field.getClassDisplayName()})rs.getObject("${field.getName()}"));
#end
	
			return pojo;
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