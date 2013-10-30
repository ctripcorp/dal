package {{product_line}}.{{domain}}.{{app_name}}.dao;

import java.sql.ResultSet;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import {{product_line}}.{{domain}}.{{app_name}}.dao.common.AbstractDAO;
import {{product_line}}.{{domain}}.{{app_name}}.dao.exception.ParametersInvalidException;
import {{product_line}}.{{domain}}.{{app_name}}.dao.param.Parameter;

public class {{dao_name}} extends AbstractDAO {
	
	private static final Logger logger = LoggerFactory.getLogger({{dao_name}}.class);

	{% for method in methods %}
	//{{method.comment}}									
	public ResultSet {{method.method_name}}(Parameter... params)
			throws Exception {

		{% if method.extra == None %}
		final String sql = "{% raw method.sql %}";
		{% else %}
		Arrays.sort(params);

		String[] placeHolder = new String[params.length];

		for (int i = 0; i < params.length; i++) {
			//First set the place holder to just one parameter
			placeHolder[i] = "?";
			
			//if and only if user pass in valid parameter, we
			//format the place holder for him
			if (params[i].getValue().isArrayValue()) {
				int batchSize = params[i].getValue().asArrayValue().size();

				if (batchSize > 0) {
					StringBuilder inClause = new StringBuilder();
					inClause.append('(');
					for (int j = 0; j < batchSize; j++) {
						inClause.append('?');
						if (j != batchSize - 1) {
							inClause.append(',');
						}
					}
					inClause.append(')');
					placeHolder[i] = inClause.toString();
				}
			}
			
		}
		final String sql = String
				.format("{% raw method.sql %}",
						(Object[]) placeHolder);
		{% end %}

		return super.fetch(null, sql, 0, params);
	}
	{% end %}

}
