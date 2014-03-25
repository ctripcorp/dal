package ${host.getPackageName()};

#foreach( $field in ${host.getPojoImports()} )
import ${field};
#end

import java.sql.Timestamp;
import com.ctrip.platform.dal.dao.DalPojo;

public class ${host.getPojoClassName()} implements DalPojo {
#foreach( $field in ${host.getFields()} )
	private ${field.getClassDisplayName()} ${field.getUncapitalizedName()};
#end
#foreach( $field in ${host.getFields()} )
	public ${field.getClassDisplayName()} get${field.getCapitalizedName()}() {
		return ${field.getUncapitalizedName()};
	}

	public void set${field.getCapitalizedName()}(${field.getClassDisplayName()} ${field.getUncapitalizedName()}) {
		this.${field.getUncapitalizedName()} = ${field.getUncapitalizedName()};
	}

#end
}