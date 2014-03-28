package ${host.getPackageName()};

#foreach( $field in ${host.getDaoImports()} )
import ${field};
#end

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalRowMapper;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DalRowMapperExtractor;
import com.ctrip.platform.dal.dao.helper.DalScalarExtractor;

public class ${host.getPojoClassName()}Dao {

	private static final String DATA_BASE = "${host.getDbName()}";
	private static final String ALL_SQL_PATTERN = "SELECT * FROM ${host.getViewName()}";
	private static final String COUNT_SQL_PATTERN = "SELECT count(1) from ${host.getViewName()}";
	private static final String PAGE_MYSQL_PATTERN = "SELECT * FROM ${host.getViewName()} WHERE LIMIT %s, %s";
	private static final String PAGE_SQL_PATTERN = "WITH CTE AS (select *, row_number() over(order by ${host.getOverColumns()} desc ) as rownum" 
			+" from ${host.getViewName()} (nolock)) select * from CTE where rownum between %s and %s";
			
	private DalClient client;
	private ${host.getPojoClassName()}RowMapper mapper;
	private DalRowMapperExtractor<${host.getPojoClassName()}> extractor;
	private DalScalarExtractor scalarExtractor;
	
	/**
	 * Initialize the instance of Hotel2GenDao
	 */
	public ${host.getPojoClassName()}Dao()
	{
		this.client = DalClientFactory.getClient(DATA_BASE);
		this.mapper = new ${host.getPojoClassName()}RowMapper();
		this.extractor = new DalRowMapperExtractor<${host.getPojoClassName()}>(this.mapper);
		this.scalarExtractor = new DalScalarExtractor();
	}

	/**
	  *Get all ${host.getPojoClassName()} instances
	  *@return 
	  *     ${host.getPojoClassName()} collection
	**/
	public List<${host.getPojoClassName()}> getAll() throws SQLException
	{
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		List<${host.getPojoClassName()}> result = null;
		result = this.client.query(ALL_SQL_PATTERN, parameters, hints, extractor);
		return result;
	}
	
	/**
	  *Get the count of ${host.getPojoClassName()} instances
	  *@return 
	  *     the ${host.getPojoClassName()} records count
	**/
	public long Count() throws SQLException
	{
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		long result = (long)this.client.query(COUNT_SQL_PATTERN, parameters, hints, scalarExtractor);
		return result;
	}
	
	public List<${host.getPojoClassName()}> getListByPage(${host.getPojoClassName()} obj, int pagesize, int pageNo) throws SQLException
	{
		if(pageNo < 1 || pagesize < 1) 
			throw new SQLException("Illigal pagesize or pageNo, pls check");
		
        StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		
		String sql = "";
#if($host.getDatabaseCategory().name() == "MySql" )
		sql = String.format(PAGE_MYSQL_PATTERN, (pageNo - 1) * pagesize, pagesize);
#else
		int fromRownum = (pageNo - 1) * pagesize + 1;
        int endRownum = pagesize * pageNo;
		sql = String.format(PAGE_SQL_PATTERN, fromRownum, endRownum);
#end
		return this.client.query(sql, parameters, hints, extractor);
	}

	/**
	  * Map the sql result-set to ${host.getPojoClassName()} instance
	**/
	private class ${host.getPojoClassName()}RowMapper implements DalRowMapper<${host.getPojoClassName()}> {

		@Override
		public ${host.getPojoClassName()} map(ResultSet rs, int rowNum) throws SQLException {
			${host.getPojoClassName()} pojo = new ${host.getPojoClassName()}();
			
#foreach( $field in ${host.getFields()} )
			pojo.set${field.getCapitalizedName()}((${field.getClassDisplayName()})rs.getObject("${field.getName()}"));
#end

			return pojo;
		}
	}
}
