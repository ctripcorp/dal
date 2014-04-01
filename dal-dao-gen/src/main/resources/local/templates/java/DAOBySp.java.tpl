package ${host.getPackageName()};

import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;

#foreach( $field in ${host.getDaoImports()} )
import ${field};
#end

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;

public class ${host.getDbName()}SpDao {
	private static final String DATA_BASE = "${host.getDbName()}";
	private DalClient client;

	public ${host.getDbName()}SpDao() {
		this.client = DalClientFactory.getClient(DATA_BASE);
	}
	
#foreach($h in $host.getSpHosts())
	public Map<String, ?> call${h.getPojoClassName()}(${h.getPojoClassName()} param) throws SQLException {
		String callString = "${h.getSpName()}(${h.getCallParameters()})";
		StatementParameters parameters = new StatementParameters();
		
#foreach($p in $h.getFields())
#if($p.getDirection().name() == "Input")
		parameters.set("${p.getName()}", ${p.getJavaTypeDisplay()}, param.get${p.getCapitalizedName()}());
#end
#if($p.getDirection().name() == "InputOutput")
		parameters.registerInOut("${p.getName()}", ${p.getJavaTypeDisplay()}, param.get${p.getCapitalizedName()}());
#end
#if($p.getDirection().name() == "Output")
		parameters.registerOut("${p.getName()}", ${p.getJavaTypeDisplay()});
#end
#end

		/* To specify returned result(not the output or inputoutput parameter)
		DalRowMapperExtractor<Map<String, Object>> extractor = new DalRowMapperExtractor<Map<String, Object>>(new DalColumnMapRowMapper());
		param = StatementParameter.newBuilder().setResultsParameter(true).setResultSetExtractor(extractor).setName("result").build();
		parameters.add(param);

		param  = StatementParameter.newBuilder().setResultsParameter(true).setName("count").build();
		parameters.add(param);
		*/
		
		DalHints hints = new DalHints();
		
		return client.call(callString, parameters, hints);
	
	}
	
#end

}