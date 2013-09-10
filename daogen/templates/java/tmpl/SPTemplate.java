package {{product_line}}.{{domain}}.{{app_name}}.dao;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import {{product_line}}.{{domain}}.{{app_name}}.dao.common.AbstractDAO;
import {{product_line}}.{{domain}}.{{app_name}}.dao.exception.ParametersInvalidException;
import {{product_line}}.{{domain}}.{{app_name}}.dao.param.Parameter;

public class {{database_name}} extends AbstractDAO {
	
	private static final Logger logger = LoggerFactory.getLogger({{database_name}}.class);

	public {{database_name}}() {
	}

	{% for sp in sp_methods %}
	//{{sp.comment}}									
	public {% if sp.action == 'fetch' %} ResultSet {% else %} int {% end %} {{sp.method_name}}(Parameter... params)
			throws Exception {
		
		final String spName = "{{sp.sp_name}}";
	
		{% if sp.action == 'fetch' %}
			return super.fetchBySp(null, spName, 0, params);
		{% else %}
			return super.executeSp(null, spName, 0, params);
		{% end %}
	}
	{% end %}

}
