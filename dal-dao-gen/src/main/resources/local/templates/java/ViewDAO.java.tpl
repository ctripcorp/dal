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
import com.ctrip.platform.dal.dao.DalResultSetExtractor;
import com.ctrip.platform.dal.dao.DalRowMapper;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.helper.DalRowMapperExtractor;
import com.ctrip.platform.dal.dao.helper.DalScalarExtractor;

public class ${host.getPojoClassName()}Dao {
	private static final String DATA_BASE = "${host.getDbName()}";
	
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
		String sql = "SELECT * FROM ${host.getViewName()}";
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		List<${host.getPojoClassName()}> result = null;
		result = this.client.query(sql, parameters, hints, new DalResultSetExtractor<List<${host.getPojoClassName()}>>(){
			@Override
			public List<${host.getPojoClassName()}> extract(ResultSet rs) throws SQLException {
				return extractor.extract(rs);
			}});
		return result;
	}
	
	/**
	  *Get the count of ${host.getPojoClassName()} instances
	  *@return 
	  *     the ${host.getPojoClassName()} records count
	**/
	public int Count() throws SQLException
	{
		String sql = "SELECT count(1) from ${host.getViewName()}  with (nolock)";
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		int result = this.client.query(sql, parameters, hints, new DalResultSetExtractor<Integer>(){
			@Override
			public Integer extract(ResultSet rs) throws SQLException {
				return (Integer)scalarExtractor.extract(rs);
			}
		});
		return result;
	}
	
	public List<${host.getPojoClassName()}> getListByPage(${host.getPojoClassName()} obj, int pagesize, int pageNo)
	{
		// TODO to be implemented
		DalHints hints = new DalHints();
		return null;
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
