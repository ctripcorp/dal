package {{product_line}}.{{domain}}.{{app_name}}.dao.freesql;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import {{product_line}}.{{domain}}.{{app_name}}.dao.common.AbstractDAO;
import {{product_line}}.{{domain}}.{{app_name}}.dao.exception.ParametersInvalidException;
import {{product_line}}.{{domain}}.{{app_name}}.dao.msg.AvailableType;

public class {{TableName}}DAO extends AbstractDAO {
	
	private static final Logger logger = LoggerFactory.getLogger({{TableName}}DAO.class);

	private Map<String, String> dbField2POJOField;

	public {{TableName}}DAO() {
		dbField2POJOField = new HashMap<String, String>();
		// dbField2POJOField.put("Name", "name");
	}

	{% for method in methods %}
	//{{method.comment}}									
	public ResultSet {{method.name}}(AvailableType... params)
			throws Exception {
		
		final int paramCount = {{method.paramCount}};

		final String sql = {{method.sql}};
		
		if(params.length != paramCount){
			throw new ParametersInvalidException(String.format(
					"Required %d parameter(s), but got %d!", 
					paramCount, params.length));
		}

		return super.{{method.action}}(null, sql, 0, params);
	}
	{% end %}

}
