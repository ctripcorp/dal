package ${host.getNameSpaceDao()};

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalQueryDao;
import com.ctrip.platform.dal.dao.DalRowMapper;
import com.ctrip.platform.dal.dao.StatementParameters;

public class ${host.getClassName()}Dao {
	private DalQueryDao queryDao;
#foreach( $method in ${host.getMethods()} )
	private ${method.getPojoClassName()}RowMapper ${method.getVariableName()}RowMapper = new ${method.getPojoClassName()}RowMapper();
#end
	public ${host.getClassName()}() {
		queryDao = new DalQueryDao(${host.getDbName()});
	}
    
#foreach($method in $host.getMethods())
	public List<${method.getClassName()}> ${method.getName()}(${method.getParameterDeclaration()}) throws SQLException {
		String sql = "${method.getSql()}";
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		int i = 1;
#foreach($p in $method.getParameters())
		parameters.set(i++, ${p.getJavaTypeDisplay()}, param${p.getName()});
#end
		//如果只需要一条记录，建议使用limit 1或者top 1，并使用SelectFirst提高性能
		return queryDao.query(sql, parameters, hints, personRowMapper);
	}

#end

#foreach( $method in ${host.getMethods()} )
	private class ${method.getPojoClassName()}RowMapper implements DalRowMapper<${method.getPojoClassName()}> {

		@Override
		public ${method.getPojoClassName()} map(ResultSet rs, int rowNum) throws SQLException {
			${method.getPojoClassName()} pojo = new ${method.getPojoClassName()}();
			
#foreach( $field in ${method.getFields()} )
			pojo.set${field.getCapitalizedName()}((${field.getClassDisplayName()})rs.getObject("${field.getName()}"));
#end

			return pojo;
		}
	}

#end
}
