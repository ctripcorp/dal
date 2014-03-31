package ${host.getPackageName()};

#foreach( $field in ${host.getDaoImports()} )
import ${field};
#end

import com.ctrip.platform.dal.dao.DalClientFactory;

public class ${host.getPojoClassName()}DaoTest {
	public static void main(String[] args) {
		try {
			/**
			* Initialize DalClientFactory.
			* The Dal.config can be specified from class-path or local file path.
			* One of both need to be enabled.
			**/			
			DalClientFactory.initClientFactory(); // load from class-path
			DalClientFactory.initClientFactory("E:/DalMult.config"); // load from file path
			
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
			
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}