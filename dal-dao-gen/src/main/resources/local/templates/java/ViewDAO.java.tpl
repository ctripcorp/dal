package ${host.getPackageName()};

#foreach( $field in ${host.getDaoImports()} )
import ${field};
#end

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalResultSetExtractor;
import com.ctrip.platform.dal.dao.DalRowMapper;
import com.ctrip.platform.dal.dao.StatementParameters;

public class ${host.getPojoClassName()}Dao {
	private static final String DATA_BASE = "${host.getDbName()}";
	private DalClient client;
	private DalRowMapper<${host.getPojoClassName()}> mapper = new ${host.getPojoClassName()}RowMapper();
	public ${host.getPojoClassName()}Dao()
	{
		this.client = DalClientFactory.getClient(DATA_BASE);
	}

	public List<${host.getPojoClassName()}> GetAll() throws SQLException
	{
		String sql = "SELECT * FROM ${host.getViewName()}";
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		List<${host.getPojoClassName()}> result = null;
		result = this.client.query(sql, parameters, hints, new DalResultSetExtractor<List<${host.getPojoClassName()}>>(){
			@Override
			public List<${host.getPojoClassName()}> extract(ResultSet rs) throws SQLException {
				List<${host.getPojoClassName()}> resultList = new ArrayList<${host.getPojoClassName()}>();
				while(rs.next())
				{
					resultList.add(mapper.map(rs, rs.getRow()));
				}
				return resultList;
			}});
		return result;
	}
	
	public int Count() throws SQLException
	{
		String sql = "SELECT count(1) from ${host.getViewName()}  with (nolock)";
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		int result = this.client.query(sql, parameters, hints, new DalResultSetExtractor<Integer>(){
			@Override
			public Integer extract(ResultSet rs) throws SQLException {
				int count = 0;
				while(rs.next())
				{
					count = rs.getInt(1);
				}
				return count;
			}
		});
		return result;
	}
	
	public List<${host.getPojoClassName()}> GetListByPage(${host.getPojoClassName()} obj, int pagesize, int pageNo)
	{
		// TODO to be implemented
		DalHints hints = new DalHints();
		return null;
	}

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
