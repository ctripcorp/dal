package $namespace;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.ctrip.platform.dao.AbstractDAO;
import com.ctrip.platform.dao.enums.DbType;
import com.ctrip.platform.dao.enums.ParameterDirection;
import com.ctrip.platform.dao.param.StatementParameter;

public class $dao_name extends AbstractDAO {

	public $dao_name() {
		logicDbName = "$database";
		servicePort = 9000;
		credentialId = "30303";
		super.init();
	}

	#foreach( $method in $methods )
	#set($parameters = $method.getParameters())
public #if( $method.getAction() == "select" )ResultSet#{else}int#end ${method.getMethodName()}#[[(]]##foreach($p in $parameters)${p.getType()} ${p.getName()}#if($foreach.count != $parameters.size()), #end#end#[[)]]# {
		List<StatementParameter> parameters = new ArrayList<StatementParameter>();
		#foreach($p in $parameters)
parameters.add(StatementParameter.newBuilder().setDbType(DbType.${JavaDbTypeMap.get($p.getType())}).setDirection(ParameterDirection.Input).setNullable(false).setIndex(1).setName("").setSensitive(false).setValue(${p.getName()}).build());
		#end
return this.#if( $method.getAction() == "select" )fetch#{else}execute#end("${method.getSqlSPName()}", parameters, null);
	}

	#end

	#foreach( $method in $sp_methods )
	#set($parameters = $method.getParameters())
public int ${method.getMethodName()}#[[(]]##foreach($p in $parameters)${p.getType()} ${p.getName()}#if($foreach.count != $parameters.size()), #end#end#[[)]]# {
		List<StatementParameter> parameters = new ArrayList<StatementParameter>();
		#foreach($p in $parameters)
parameters.add(StatementParameter.newBuilder().setDbType(DbType.${JavaDbTypeMap.get($p.getType())}).setDirection(ParameterDirection.Input).setNullable(false).setIndex(1).setName("").setSensitive(false).setValue(${p.getName()}).build());

		#end
return this.executeSP("${method.getSqlSPName()}", parameters, null);
	}

	#end

}
