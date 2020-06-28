package ${host.getPackageName()}.dao;

#foreach( $field in ${host.getDaoImports()} )
import ${field};
#end
import ${host.getPackageName()}.entity.${host.getPojoClassName()};

public class ${host.getDbSetName()}SpDao {
	private static final String DATA_BASE = "${host.getDbSetName()}";
	private DalClient client;

	public ${host.getDbSetName()}SpDao() {
		this.client = DalClientFactory.getClient(DATA_BASE);
	}
	
#foreach($h in $host.getSpHosts())
#if($h.isSp3())
	/**
	 * Batch call ${h.getSpName()} Store Procedure
	 */
	public int[] batchCall${h.getPojoClassName()}(${h.getPojoClassName()}[] params, DalHints hints) throws SQLException{
		if (null == params || params.length == 0)
			return new int[]{};
		String callString = "{call ${h.getSpName()}(${h.getCallParameters()})}";
		StatementParameters[] parametersList= new StatementParameters[params.length];
		for(int i = 0; i< params.length; i++){
			StatementParameters parameters = new StatementParameters();
#foreach($p in $h.getFields())
		    parameters.set("${p.getName()}", ${p.getJavaTypeDisplay()}, params[i].get${p.getCapitalizedName()}());		
#end
	       parametersList[i] = parameters;
		}
		
		return this.client.batchCall(callString, parametersList, hints);
	}
#end
	
	/**
	 * Call ${h.getSpName()} Store Procedure
	 */
	public Map<String, ?> call${h.getPojoClassName()}(${h.getPojoClassName()} param, DalHints hints) throws SQLException {
		String callString = "{call ${h.getSpName()}(${h.getCallParameters()})}";
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
		parameters.setResultsParameter("result", extractor);
		
		param  = StatementParameter.newBuilder().setResultsParameter(true).setName("count").build();
		parameters.add(param);
		*/
		
		hints = DalHints.createIfAbsent(hints);
		
		return client.call(callString, parameters, hints);
	
	}
	
#end

}