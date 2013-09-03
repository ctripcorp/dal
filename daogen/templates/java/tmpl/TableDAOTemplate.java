package {{product_line}}.{{domain}}.{{app_name}}.dao.tabledao;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import {{product_line}}.{{domain}}.{{app_name}}.dao.common.AbstractDAO;
import {{product_line}}.{{domain}}.{{app_name}}.dao.exception.ParametersInvalidException;
import {{product_line}}.{{domain}}.{{app_name}}.dao.msg.AvailableType;

public class {{dao_name}} extends AbstractDAO {
	
	private static final Logger logger = LoggerFactory.getLogger({{dao_name}}.class);

	{% for method in methods %}
	//{{method.comment}}									
	public {% if method.action == 'fetch' %} ResultSet {% else %} int {% end %} {{method.method_name}}(AvailableType... params)
			throws Exception {
		
		final int paramCount = {{method.paramCount}};

		final String sql = "{% raw method.sql %}";
		
		if(params.length != paramCount){
			throw new ParametersInvalidException(String.format(
					"Required %d parameter(s), but got %d!", 
					paramCount, params.length));
		}

		return super.{{method.action}}(null, sql, 0, params);
	}
	{% end %}

	{% for sp in sp_methods %}
	//{{sp.comment}}									
	public {% if sp.action == 'fetch' %} ResultSet {% else %} int {% end %} {{sp.method_name}}(AvailableType... params)
			throws Exception {
		
		final int paramCount = {{sp.paramCount}};

		final String spName = "{{sp.sp_name}}";
		
		if(params.length != paramCount){
			throw new ParametersInvalidException(String.format(
					"Required %d parameter(s), but got %d!", 
					paramCount, params.length));
		}

		{% if sp.action == 'fetch' %}
			return super.fetchBySp(null, spName, 0, params);
		{% else %}
			return super.executeSp(null, spName, 0, params);
		{% end %}
	}
	{% end %}

}
