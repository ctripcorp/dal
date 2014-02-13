
package $namespace;

import com.ctrip.platform.dal.dao.DalPojo;

public class $table_name implements DalPojo {

	#foreach( $field in $fields )

	private ${field.getType()} ${field.getName()};

	public ${field.getType()} get${field.getName()}#[[(){]]#
		return ${field.getName()};
	#[[}]]#

	//#[[(]]#表示(不会被Verlocity解析
	public void set${field.getName()}#[[(]]#${field.getType()} ${field.getName()}#[[)]]##[[{]]#
		this.${field.getName()} = ${field.getName()};
	#[[}]]#

	#end

}