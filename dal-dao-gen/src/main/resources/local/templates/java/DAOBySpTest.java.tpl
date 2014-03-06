package ${host.getPackageName()};

#foreach( $field in ${host.getDaoImports()} )
import ${field};
#end

import com.ctrip.platform.dal.dao.DalClientFactory;

public class ${host.getPojoClassName()}DaoTest {
	public static void main(String[] args) {
		try {
			// Initialize DalClientFactory
			DalClientFactory.initClientFactory("${host.getDbName()}");
			${host.getPojoClassName()}Dao dao = new ${host.getPojoClassName()}Dao();
			
			${host.getPojoClassName()} param = new ${host.getPojoClassName()}();
			
			// Set test value here
#foreach($p in $host.getFields())
#if($p.getDirection().name() == "Input")
			param.set${p.getCapitalizedName()}(null);
#end
#if($p.getDirection().name() == "InputOutput")
			param.set${p.getCapitalizedName()}(null);
#end
#if($p.getDirection().name() == "Output")
			parameters.registerOut(i++, ${p.getJavaTypeDisplay()}, "${p.getName()}");
#end
#end

			Map<String, ?> result = dao.call${host.getPojoClassName()}(param);
			for(String key: result.keySet()) {
				System.out.print("Key: " + key);
				System.out.println(" Value: " + result.get(Key));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}