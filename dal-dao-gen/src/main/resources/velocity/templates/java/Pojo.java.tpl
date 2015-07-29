package ${host.getPackageName()};

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import com.ctrip.platform.dal.dao.annotation.Type;
import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Sensitive;
import java.sql.Types;
#foreach( $field in ${host.getPojoImports()} )
import ${field};
#end

import com.ctrip.platform.dal.dao.DalPojo;

@Entity
@Database(name="$!{host.getDbSetName()}")
@Table(name="$!{host.getTableName()}")
public class ${host.getPojoClassName()} implements DalPojo {
#foreach( $field in ${host.getFields()} )
	
#if(${field.isPrimary()})
	@Id
#end
	@Column(name="${field.getName()}")
#if(${field.isIdentity()})
	@GeneratedValue(strategy = GenerationType.AUTO)
#end
	@Type(value=${field.getJavaTypeDisplay()})
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