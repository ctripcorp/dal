package ${host.getNameSpaceDao()};

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalQueryDao;
import com.ctrip.platform.dal.dao.DalRowMapper;
import com.ctrip.platform.dal.dao.StatementParameters;

public class ${host.getClassName()}TestDao {
	public static void main(String[] args) {
		try {
			// Initialize DalClientFactory
			DalClientFactory.initClientFactory("${host.getDbName()}");
			${host.getClassName()}Dao dao = new ${host.getClassName()}Dao();
		
#foreach( $method in ${host.getMethods()} )
			// Test ${method.getName()}
#foreach($p in $method.getParameters())
			${p.getClassDisplayName()} ${p.getName()} = ${p.getValidationValue()};// Test value here
#end
			List<${method.getPojoClassName()}> ${method.getPojoClassName()}s = dao.${method.getName()}(${method.getParameterNames()});

#end
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}

}
