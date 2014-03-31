package ${host.getPackageName()};

#foreach( $field in ${host.getDaoImports()} )
import ${field};
#end

import com.ctrip.platform.dal.dao.DalClientFactory;

public class ${host.getClassName()}TestDao {
	public static void main(String[] args) {
		try {
			/**
			* Initialize DalClientFactory.
			* The Dal.config can be specified from class-path or local file path.
			* One of both need to be enabled.
			**/			
			DalClientFactory.initClientFactory(); // load from class-path
			DalClientFactory.initClientFactory("E:/DalMult.config"); // load from file path
			
			${host.getClassName()}Dao dao = new ${host.getClassName()}Dao();
		
#foreach( $method in ${host.getMethods()} )
			// Test ${method.getName()}
#foreach($p in $method.getParameters())
			${p.getClassDisplayName()} ${p.getName()} = ${p.getValidationValue()};// Test value here
#end
			List<${method.getPojoClassName()}> ${method.getPojoClassName()}s = dao.${method.getName()}(${method.getParameterNames()});

#end
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

}
