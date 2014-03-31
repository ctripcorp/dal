package ${host.getPackageName()};

#foreach( $field in ${host.getDaoImports()} )
import ${field};
#end

import com.ctrip.platform.dal.dao.DalClientFactory;
import java.util.List;

public class ${host.getPojoClassName()}DaoTest {
	public static void main(String[] args) {
		try {
			/**
			* Initialize DalClientFactory.
			* The Dal.config can be specified from class-path or local file path.
			* One of both need to be enabled.
			**/			
			DalClientFactory.initClientFactory(); // load from class-path
			DalClientFactory.initClientFactoryBy("E:/DalMult.config"); // load from file path
			
			${host.getPojoClassName()}Dao dao = new ${host.getPojoClassName()}Dao();
			
			System.out.println(dao.Count());
			List<${host.getPojoClassName()}> ls = dao.getAll();
			if(null != ls)
				System.out.println(ls.size());
			
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}