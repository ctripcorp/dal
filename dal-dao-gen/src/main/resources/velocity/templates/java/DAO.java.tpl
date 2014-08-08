package ${host.getPackageName()};

#foreach( $field in ${host.getDaoImports()} )
import ${field};
#end

public class ${host.getPojoClassName()}Dao {
	private static final String DATA_BASE = "${host.getDbName()}";
#if($host.getDatabaseCategory().name() == "MySql")
	private static final String COUNT_SQL_PATTERN = "SELECT count(1) from ${host.getTableName()}";
	private static final String ALL_SQL_PATTERN = "SELECT * FROM ${host.getTableName()}";
	private static final String PAGE_MYSQL_PATTERN = "SELECT * FROM ${host.getTableName()} LIMIT %s, %s";
#else
	private static final String COUNT_SQL_PATTERN = "SELECT count(1) from ${host.getTableName()} WITH (NOLOCK)";
	private static final String ALL_SQL_PATTERN = "SELECT * FROM ${host.getTableName()} WITH (NOLOCK)";
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
#if($host.getSpInsert().isExist() || $host.getSpDelete().isExist() ||$host.getSpUpdate().isExist())
	private static final String RET_CODE = "retcode";
	private static final String UPDATE_COUNT = "update_count";
#end
#end
	
	private DalParser<${host.getPojoClassName()}> parser = new ${host.getPojoClassName()}Parser();
	private DalScalarExtractor extractor = new DalScalarExtractor();
	private DalRowMapperExtractor<${host.getPojoClassName()}> rowextractor = null;
	private DalTableDao<${host.getPojoClassName()}> client;
	private DalQueryDao queryDao = null;
	private DalClient baseClient;

	public ${host.getPojoClassName()}Dao() {
		this.client = new DalTableDao<${host.getPojoClassName()}>(parser);
#if($host.getDatabaseCategory().name() == "MySql")
		this.client.setDelimiter('`','`');
#else
		this.client.setDelimiter('[',']');
		this.client.setFindTemplate("SELECT * FROM %s WHERE WITH (NOLOCK) %s");
#end
		this.queryDao = new DalQueryDao(DATA_BASE);
		this.rowextractor = new DalRowMapperExtractor<${host.getPojoClassName()}>(parser); 
		this.baseClient = DalClientFactory.getClient(DATA_BASE);
	}
#if($host.hasPk())
#if($host.isIntegerPk())
	/**
	 * Query ${host.getPojoClassName()} by the specified ID
	 * The ID must be a number
	**/
	public ${host.getPojoClassName()} queryByPk(Number id, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryByPk(id, hints);
	}
#else
	/**
	 * Query ${host.getPojoClassName()} by complex primary key
	**/
	public ${host.getPojoClassName()} queryByPk(${host.getPkParameterDeclaration()})
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		${host.getPojoClassName()} pk = new ${host.getPojoClassName()}();		
#foreach( $field in ${host.getPrimaryKeys()} )
		pk.set${field.getCapitalizedName()}(${field.getUncapitalizedName()});
#end
		return client.queryByPk(pk, hints);
	}
#end
    /**
	 * Query ${host.getPojoClassName()} by ${host.getPojoClassName()} instance which the primary key is set
	**/
	public ${host.getPojoClassName()} queryByPk(${host.getPojoClassName()} pk, DalHints hints)
			throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		return client.queryByPk(pk, hints);
	}
#end
	/**
	 * Get the records count
	**/
	public int count(DalHints hints) throws SQLException
	{
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		Number result = (Number)this.baseClient.query(COUNT_SQL_PATTERN, parameters, hints, extractor);
		return result.intValue();
	}
	
	/**
	 * Query ${host.getPojoClassName()} with paging function
	 * The pageSize and pageNo must be greater than zero.
	**/
	public List<${host.getPojoClassName()}> queryByPage(int pageSize, int pageNo, DalHints hints)  throws SQLException {
		if(pageNo < 1 || pageSize < 1) 
			throw new SQLException("Illigal pagesize or pageNo, pls check");	
        StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
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
	
	/**
	 * Get all records in the whole table
	**/
	public List<${host.getPojoClassName()}> getAll(DalHints hints) throws SQLException
	{
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		List<${host.getPojoClassName()}> result = null;
		result = this.baseClient.query(ALL_SQL_PATTERN, parameters, hints, rowextractor);
		return result;
	}

#if($host.getSpInsert().isExist())
	/**
	 * SP Insert
	**/
	public int insert(DalHints hints, ${host.getPojoClassName()} daoPojo) throws SQLException {
		if(null == daoPojo)
			return 0;
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
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

#if($host.getSpInsert().getType=="sp3")
	/**
	 * Batch insert without out parameters
	 * Return how many rows been affected for each of parameters
	**/
	public int[] insert(DalHints hints, ${host.getPojoClassName()}...daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.length == 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		String callSql = client.buildCallSql(INSERT_SP_NAME, parser.getFields(daoPojos[0]).size());
		StatementParameters[] parametersList = new StatementParameters[daoPojos.length];
		for(int i = 0; i< daoPojos.length; i++){
			StatementParameters parameters = new StatementParameters();
			client.addParametersByName(parameters, parser.getFields(daoPojos[i]));
			parametersList[i] = parameters;
		}
		return baseClient.batchCall(callSql, parametersList, hints);
	}
#end

#else
	/**
	 * SQL insert
	 * Note: there must be one non-null field in daoPojo
	**/
	public void insert(DalHints hints, ${host.getPojoClassName()}...daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.length <= 0)
			return;
		hints = DalHints.createIfAbsent(hints);
		client.insert(hints, null, daoPojos);
	}
	
	/**
	 * SQL insert with batch mode
	**/
	public int[] batchInsert(DalHints hints, ${host.getPojoClassName()}...daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.length == 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.batchInsert(hints, daoPojos);
	}

	/**
	 * SQL insert with keyHolder
	 * Note: there must be one non-null field in daoPojo
	**/
	public void insert(DalHints hints, KeyHolder keyHolder, ${host.getPojoClassName()}...daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.length <= 0)
			return;
		hints = DalHints.createIfAbsent(hints);
		client.insert(hints, keyHolder, daoPojos);
	}
#end

#if($host.getSpDelete().isExist())
	/**
	 * SP delete
	**/
	public int delete(DalHints hints, ${host.getPojoClassName()} daoPojo) throws SQLException {
		if(null == daoPojo)
			return 0;
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
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
	
#if($host.getSpDelete() == "sp3")
	/**
	 * Batch SP delete without out parameters
	 * Return how many rows been affected for each of parameters
	 */
	public int[] delete(DalHints hints, ${host.getPojoClassName()}...daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.length == 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		String callSql = client.buildCallSql(DELETE_SP_NAME, parser.getFields(daoPojos[0]).size());
		StatementParameters[] parametersList = new StatementParameters[daoPojos.length];
		for(int i = 0; i< daoPojos.length; i++){
			StatementParameters parameters = new StatementParameters();
			client.addParametersByName(parameters, parser.getFields(daoPojos[i]));
			parametersList[i] = parameters;
		}
		return baseClient.batchCall(callSql, parametersList, hints);
	}
#end

#else
	/**
	 * SQL delete
	 * Note: there must be one non-null field in daoPojo
	**/
	public void delete(DalHints hints, ${host.getPojoClassName()}...daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.length <= 0)
			return;
		hints = DalHints.createIfAbsent(hints);
		client.delete(hints, daoPojos);
	}
	
	/**
	 * SQL delete with batch mode
	**/
	public int[] batchDelete(DalHints hints, ${host.getPojoClassName()}...daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.length <= 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.batchDelete(hints, daoPojos);
	}
#end

#if($host.getSpUpdate().isExist())
	/**
	 * SP update
	**/
	public int update(DalHints hints, ${host.getPojoClassName()} daoPojo) throws SQLException {
		if(null == daoPojo)
			return 0;
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
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
#* The batch sp update has issue 
	/**
	 * Batch SP update without out parameters
	 * Return how many rows been affected for each of parameters
	 */
	public int[] update(DalHints hints, ${host.getPojoClassName()}... daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.length == 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		String callSql = client.buildCallSql(UPDATE_SP_NAME, parser.getFields(daoPojos[0]).size());
		StatementParameters[] parametersList = new StatementParameters[daoPojos.length];
		for(int i = 0; i< daoPojos.length; i++){
			StatementParameters parameters = new StatementParameters();
			client.addParametersByName(parameters, parser.getFields(daoPojos[i]));
			parametersList[i] = parameters;
		}
		return baseClient.batchCall(callSql, parametersList, hints);
	}
*#

#else
	/**
	 * SQL update
	 * Note: there must be one non-null field in daoPojo
	**/
	public void update(DalHints hints, ${host.getPojoClassName()}...daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.length <= 0)
			return;
		hints = DalHints.createIfAbsent(hints);
		client.update(hints, daoPojos);
	}
#end

#foreach($method in $host.getMethods())
		/**
		 * ${method.getComments()}
		**/
#if($method.getCrud_type() == "select")
##简单类型并且返回值是List
#if($method.isSampleType() && $method.isReturnList())
	public List<${method.getPojoClassName()}> ${method.getName()}(${method.getParameterDeclaration()}) throws SQLException {
#if($method.isPaging())
		String sqlPattern = "${method.getPagingSql($host.getDatabaseCategory())}";
		String sql = String.format(sqlPattern, (pageNo - 1) * pageSize + 1, pageSize * pageNo);
#else
		String sql = "${method.getSql()}";
#end
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
#if($method.hasParameters())
		int i = 1;
#foreach($p in $method.getParameters())
		parameters.set(i++, ${p.getJavaTypeDisplay()}, ${p.getAlias()});
#end
#end
		return queryDao.query(sql, parameters, hints, ${method.getPojoClassName()}.class);
	}
#end
##简单类型并且返回值为Single
#if($method.isSampleType() && $method.isReturnSingle())
	public ${method.getPojoClassName()} ${method.getName()}(${method.getParameterDeclaration()}) throws SQLException {
		String sql = "${method.getSql()}";
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
#if($method.hasParameters())
		int i = 1;
#foreach($p in $method.getParameters())
		parameters.set(i++, ${p.getJavaTypeDisplay()}, ${p.getAlias()});
#end
#end
		return queryDao.queryForObjectNullable(sql, parameters, hints, ${method.getPojoClassName()}.class);
	}
#end
##简单类型且返回值为First
#if($method.isSampleType() && $method.isReturnFirst())
	public ${method.getPojoClassName()} ${method.getName()}(${method.getParameterDeclaration()}) throws SQLException {
		String sql = "${method.getSql()}";
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
#if($method.hasParameters())
		int i = 1;
#foreach($p in $method.getParameters())
		parameters.set(i++, ${p.getJavaTypeDisplay()}, ${p.getAlias()});
#end
#end
		return queryDao.queryFirstNullable(sql, parameters, hints, ${method.getPojoClassName()}.class);
	}
#end
##实体类型并且返回值为List
#if($method.isReturnList() && !$method.isSampleType())
	public List<${host.getPojoClassName()}> ${method.getName()}(${method.getParameterDeclaration()}) throws SQLException {
#if($method.isPaging())
		String sqlPattern = "${method.getPagingSql($host.getDatabaseCategory())}";
		String sql = String.format(sqlPattern, (pageNo - 1) * pageSize + 1, pageSize * pageNo);
#else
		String sql = "${method.getSql()}";
#end
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
#if($method.hasParameters())
		int i = 1;
#foreach($p in $method.getParameters())
		parameters.set(i++, ${p.getJavaTypeDisplay()}, ${p.getAlias()});
#end
#end
		return queryDao.query(sql, parameters, hints, parser);
	}
#end
##实体类型且返回Signle
#if($method.isReturnSingle() && !$method.isSampleType())
	public ${host.getPojoClassName()} ${method.getName()}(${method.getParameterDeclaration()}) throws SQLException {
		String sql = "${method.getSql()}";
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
#if($method.hasParameters())
		int i = 1;
#foreach($p in $method.getParameters())
		parameters.set(i++, ${p.getJavaTypeDisplay()}, ${p.getAlias()});
#end
#end
		return queryDao.queryForObjectNullable(sql, parameters, hints, parser);
	}
#end
##实体类型且返回First
#if($method.isReturnFirst() && !$method.isSampleType())
	public ${host.getPojoClassName()} ${method.getName()}(${method.getParameterDeclaration()}) throws SQLException {
		String sql = "${method.getSql()}";
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
#if($method.hasParameters())
		int i = 1;
#foreach($p in $method.getParameters())
		parameters.set(i++, ${p.getJavaTypeDisplay()}, ${p.getAlias()});
#end
#end
		return queryDao.queryFirstNullable(sql, parameters, hints, parser);
	}
#end
#else
	public int ${method.getName()}(${method.getParameterDeclaration()}) throws SQLException {
#if(${method.getInClauses()} != "")
		String sql = SQLParser.parse("${method.getSql()}",${method.getInClauses()});
#else
		String sql = SQLParser.parse("${method.getSql()}");
#end
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		
		int i = 1;
#foreach($p in $method.getParameters())
#if($p.isInParameter())
		i = parameters.setInParameter(i, ${p.getJavaTypeDisplay()}, ${p.getAlias()});
#else
		parameters.set(i++, ${p.getJavaTypeDisplay()}, ${p.getAlias()});
#end
#end
		return baseClient.update(sql, parameters, hints);
	}
#end
#end


#if($host.isSp() && ($host.getSpInsert().isExist() || $host.getSpDelete().isExist() ||$host.getSpUpdate().isExist()))
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
	public static class ${host.getPojoClassName()}Parser extends AbstractDalParser<${host.getPojoClassName()}> {
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