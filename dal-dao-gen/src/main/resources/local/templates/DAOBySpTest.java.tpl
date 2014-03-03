package ${host.getPackageName()};

#foreach( $field in ${host.getDaoImports()} )
import ${field};
#end

import com.ctrip.platform.dal.dao.DalClientFactory;

public class ${host.getPojoClassName()}DaoTest {
	public static void main(String[] args) {
		// Initialize DalClientFactory
		
		${host.getPojoClassName()}Dao dao = new ${host.getPojoClassName()}Dao();
		try {
			${host.getPojoClassName()} param = new ${host.getPojoClassName()}();
#foreach($p in $host.getFields())
#if($p.getDirection().name() == "Input")
			param.set${p.getCapitalizedName()}(null);// you value here
#end
#if($p.getDirection().name() == "InputOutput")
			param.set${p.getCapitalizedName()}(null);// you value here
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
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}
}