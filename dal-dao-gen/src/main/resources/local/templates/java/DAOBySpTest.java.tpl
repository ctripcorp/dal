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

#set($count = 0)
#foreach($h in $host.getSpHosts())
#set($count = $count+1)
			//Test call${h.getPojoClassName()} method
			${h.getPojoClassName()} param${count} = new ${h.getPojoClassName()}();	
			// Set test value here
			//param.setXXX(value);
			
			Map<String, ?> result${count} = dao.call${h.getPojoClassName()}(param${count});
			for(String key: result${count}.keySet()) {
				System.out.print("Key: " + key);
				System.out.println(" Value: " + result${count}.get(key));
			}
#end
			
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}