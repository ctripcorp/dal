package ${host.getPackageName()};

#foreach( $field in ${host.getDaoImports()} )
import ${field};
#end

import com.ctrip.platform.dal.dao.DalClientFactory;

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
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

}
