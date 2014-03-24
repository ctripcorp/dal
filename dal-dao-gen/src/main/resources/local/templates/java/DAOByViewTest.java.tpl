package ${host.getPackageName()};

#foreach( $field in ${host.getDaoImports()} )
import ${field};
#end

import com.ctrip.platform.dal.dao.DalClientFactory;
import java.util.List;

public class ${host.getPojoClassName()}DaoTest {
	public static void main(String[] args) {
		try {
			// Initialize DalClientFactory
			DalClientFactory.initClientFactory("${host.getDbName()}");
			${host.getPojoClassName()}Dao dao = new ${host.getPojoClassName()}Dao();
			
			System.out.printlin(dao.Count());
			List<${host.getPojoClassName()}> ls = dao.GetAll();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}