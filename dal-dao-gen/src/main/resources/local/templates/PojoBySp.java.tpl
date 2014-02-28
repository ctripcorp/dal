package ${host.getDaoNamespace()};

#foreach( $field in ${host.getPojoImports()} )
import ${field};
#end

import com.ctrip.platform.dal.dao.DalPojo;

// ${host.getSpName()}
public class ${host.getClassName()} implements DalPojo {

#foreach( $field in ${host.getFields()} )
	private ${field.getJavaClass().getSimpleName()} ${field.getName()};
#end

#foreach( $field in ${host.getFields()} )
	public ${field.getJavaClass().getSimpleName()} get${field.getName()}#[[(){]]#
		return ${field.getName()};
	#[[}]]#

	public void set${field.getName()}#[[(]]#${field.getJavaClass().getSimpleName()} ${field.getName()}#[[)]]##[[{]]#
		this.${field.getName()} = ${field.getName()};
	#[[}]]#

#end
}