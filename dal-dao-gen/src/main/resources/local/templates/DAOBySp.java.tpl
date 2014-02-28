package ${host.getPackageName()};

#foreach( $field in ${host.getDaoImports()} )
import ${field};
#end

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.StatementParameters;

public class ${host.getClassName()}Dao {
	private DalClient client;

	public ${host.getClassName()}Dao(String logicDbName) {
		this.client = DalClientFactory.getClient(logicDbName);
	}
	
	public Map<String, ?> call${host.getClassName()}(${host.getClassName()} param) throws SQLException {
		String callString = "${host.getSpName()}";
		
		StatementParameters parameters = new StatementParameters();
		int i = 1;
#foreach($p in $host.getParameters())
#if($p.getDirection().name() == "Input")
		parameters.set(i, ${p.getJavaTypeDisplay()}, param.get${p.getCapitalizedName()});
#end
#if($p.getDirection().name() == "InputOutput")
		parameters.registerInOut(i, ${p.getJavaTypeDisplay()}, ${p.getName()}, param.get${p.getCapitalizedName()});
#end
#if($p.getDirection().name() == "Output")
		parameters.registerOut(i, ${p.getJavaTypeDisplay()}, ${p.getName()});
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
}