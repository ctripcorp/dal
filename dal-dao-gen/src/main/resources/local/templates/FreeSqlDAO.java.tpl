package ${host.getNameSpaceDao()};

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalQueryDao;
import com.ctrip.platform.dal.dao.DalRowMapper;
import com.ctrip.platform.dal.dao.StatementParameters;

import com.ctrip.platform.dal.tester.person.Person;

public class ${host.getClassName()} {
	private DalQueryDao queryDao;

#foreach($p in $method.getParameters())
	private ${method.getClassName()}RowMapper ${method.getVariableName()}RowMapper = new ${method.getClassName()}RowMapper();
#end

	public ${host.getClassName()}(String logicDbName) {
		queryDao = new DalQueryDao(logicDbName);
	}
    
#foreach($method in $host.getMethods())
	public List<${method.getClassName()}> ${method.getName()}(${method.getParameterDeclaration()}) throws SQLException {
		String sql = "${method.getSql()}";
		StatementParameters parameters = new StatementParameters();
		DalHints hints = new DalHints();
		int i = 1;
#foreach($p in $method.getParameters())  
		parameters.set(i++, ${p.getSqlType()}, ${p.getName());
#end
		//如果只需要一条记录，建议使用limit 1或者top 1，并使用SelectFirst提高性能
		return queryDao.query(sql, parameters, hints, personRowMapper);
	}

#end
#foreach($method in $host.getMethods())
	private class ${method.getClassName()}RowMapper implements DalRowMapper<${method.getClassName()}> {

		@Override
		public ${method.getClassName()} map(ResultSet rs, int rowNum) throws SQLException {
			${method.getClassName()} pojo = new ${method.getClassName()}();
			
#foreach( $field in ${method.getFields()} )
			pojo.set${field.getName()}((${field.getJavaClass().getSimpleName()})rs.getObject("${field.getName()}"));
#end

			return pojo;
		}
	}

#end
}
