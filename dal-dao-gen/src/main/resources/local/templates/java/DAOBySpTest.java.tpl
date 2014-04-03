package ${host.getPackageName()};

#foreach( $field in ${host.getTestImports()} )
import ${field};
#end

import com.ctrip.platform.dal.dao.DalClientFactory;

public class ${host.getDbName()}SpDaoTest {
	public static void main(String[] args) {
		try {
			/**
			* Initialize DalClientFactory.
			* The Dal.config can be specified from class-path or local file path.
			* One of follow three need to be enabled.
			**/
			//DalClientFactory.initPrivateFactory(); //Load from class-path connections.properties
			DalClientFactory.initClientFactory(); // load from class-path Dal.config
			//DalClientFactory.initClientFactory("E:/DalMult.config"); // load from the specified Dal.config file path
			
			${host.getDbName()}SpDao dao = new ${host.getDbName()}SpDao();
			
#foreach($h in $host.getSpHosts())
			//Test call${h.getPojoClassName()} method
			${h.getPojoClassName()} param = new ${h.getPojoClassName()}();	
			// Set test value here
			//param.setXXX(value);
			
			Map<String, ?> result = dao.call${h.getPojoClassName()}(param);
			for(String key: result.keySet()) {
				System.out.print("Key: " + key);
				System.out.println(" Value: " + result.get(key));
			}
#break
#end
			
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}