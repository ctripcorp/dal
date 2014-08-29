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
#end
#end
	
	private DalParser<${host.getPojoClassName()}> parser = new ${host.getPojoClassName()}Parser();
	private DalScalarExtractor extractor = new DalScalarExtractor();
	private DalRowMapperExtractor<${host.getPojoClassName()}> rowextractor = null;
	private DalTableDao<${host.getPojoClassName()}> client;
#if($host.hasMethods())
	private DalQueryDao queryDao = null;
#end
	private DalClient baseClient;
	
#parse("templates/java/dao/standard/method.constructor.tpl")
#parse("templates/java/dao/standard/method.queryByPk.tpl")
#parse("templates/java/dao/standard/method.count.tpl")
#parse("templates/java/dao/standard/method.queryByPage.tpl")
#parse("templates/java/dao/standard/method.getAll.tpl")
#parse("templates/java/dao/standard/method.Insert.sp.tpl")
#parse("templates/java/dao/standard/method.Insert.notSp.tpl")
#parse("templates/java/dao/standard/method.Delete.sp.tpl")
#parse("templates/java/dao/standard/method.Delete.notSp.tpl")
#parse("templates/java/dao/standard/method.Update.sp.tpl")
#parse("templates/java/dao/standard/method.Update.notSp.tpl")
#parse("templates/java/dao/standard/method.scalar.tpl")

#if($host.isSp() && ($host.getSpInsert().isExist() || $host.getSpDelete().isExist() ||$host.getSpUpdate().isExist()))
	private String prepareSpCall(String SpName, StatementParameters parameters, Map<String, ?> fields) {
		client.addParametersByName(parameters, fields);
		String callSql = client.buildCallSql(SpName, fields.size());
		parameters.setResultsParameter(RET_CODE, extractor);
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