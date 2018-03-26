package ${host.getPackageName()}.dao;

#foreach( $field in ${host.getDaoImports()} )
import ${field};
#end
import ${host.getPackageName()}.entity.${host.getPojoClassName()};

import com.ctrip.platform.dal.dao.helper.DalDefaultJpaMapper;

public class ${host.getPojoClassName()}Dao {

	private static final String DATA_BASE = "${host.getDbSetName()}";
	
#if($host.getDatabaseCategory().name() == "MySql")
	private static final String ALL_SQL_PATTERN = "SELECT * FROM ${host.getViewName()}";
	private static final String COUNT_SQL_PATTERN = "SELECT count(1) from ${host.getViewName()}";
	private static final String PAGE_MYSQL_PATTERN = "SELECT * FROM ${host.getViewName()} LIMIT %s, %s";
#else
	private static final String ALL_SQL_PATTERN = "SELECT * FROM ${host.getViewName()} WITH (NOLOCK)";
	private static final String COUNT_SQL_PATTERN = "SELECT count(1) from ${host.getViewName()} WITH (NOLOCK)";
	private static final String PAGE_SQL_PATTERN = "select * from ${host.getViewName()} (nolock) order by ${host.getOverColumns()} desc OFFSET %s ROWS FETCH NEXT %s ROWS ONLY";
#end
			
	private DalClient client;
	private DalRowMapper<${host.getPojoClassName()}> mapper;
	private DalRowMapperExtractor<${host.getPojoClassName()}> extractor;
	private DalScalarExtractor scalarExtractor;

	public ${host.getPojoClassName()}Dao() throws SQLException {
		this.client = DalClientFactory.getClient(DATA_BASE);
		this.mapper = new DalDefaultJpaMapper(${host.getPojoClassName()}.class);
		this.extractor = new DalRowMapperExtractor<${host.getPojoClassName()}>(this.mapper);
		this.scalarExtractor = new DalScalarExtractor();
	}

	/**
	  *Get all ${host.getPojoClassName()} instances
	  *@return 
	  *     ${host.getPojoClassName()} collection
	**/
	public List<${host.getPojoClassName()}> getAll(DalHints hints) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		List<${host.getPojoClassName()}> result = null;
		result = this.client.query(ALL_SQL_PATTERN, parameters, hints, extractor);
		return result;
	}
	
	/**
	  *Get the count of ${host.getPojoClassName()} instances
	  *@return 
	  *     the ${host.getPojoClassName()} records count
	**/
	public int count(DalHints hints) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);	
		Number result = (Number)this.client.query(COUNT_SQL_PATTERN, parameters, hints, scalarExtractor);
		return result.intValue();
	}
	
	public List<${host.getPojoClassName()}> getListByPage(int pagesize, int pageNo, DalHints hints) throws SQLException {
		if(pageNo < 1 || pagesize < 1) 
			throw new SQLException("Illigal pagesize or pageNo, pls check");
		
        StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);	
		
		String sql = "";
#if($host.getDatabaseCategory().name() == "MySql" )
		sql = String.format(PAGE_MYSQL_PATTERN, (pageNo - 1) * pagesize, pagesize);
#else
		int fromRownum = (pageNo - 1) * pagesize;
        int endRownum = pagesize;
		sql = String.format(PAGE_SQL_PATTERN, fromRownum, endRownum);
#end
		return this.client.query(sql, parameters, hints, extractor);
	}

}