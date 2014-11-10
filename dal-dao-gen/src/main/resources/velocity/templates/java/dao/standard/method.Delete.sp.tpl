#if($host.getSpDelete().isExist() && $host.generateAPI(20))
	/**
	 * SP delete
	**/
	public int delete(DalHints hints, ${host.getPojoClassName()} daoPojo) throws SQLException {
		if(null == daoPojo)
			return 0;
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		String callSql = prepareSpCall(BASIC_DELETE_SP_NAME, parameters, parser.getPrimaryKeys(daoPojo));
#foreach($p in $host.getSpDelete().getParameters())
#if($p.getDirection().name() == "InputOutput")
		parameters.registerInOut("${p.getName()}", ${p.getJavaTypeDisplay()}, daoPojo.get${p.getCapitalizedName()}());
#end
#if($p.getDirection().name() == "Output")
		parameters.registerOut("${p.getName()}", ${p.getJavaTypeDisplay()});
#end
#end
		Map<String, ?> results = baseClient.call(callSql, parameters, hints);
#foreach($p in $host.getSpDelete().getParameters())
#if($p.getDirection().name() == "InputOutput")
		${p.getClassDisplayName()} ${p.getUncapitalizedName()} = (${p.getClassDisplayName()})parameters.get("${p.getName()}", ParameterDirection.InputOutput).getValue();
#end
#if($p.getDirection().name() == "Output")
		${p.getClassDisplayName()} ${p.getUncapitalizedName()} = (${p.getClassDisplayName()})parameters.get("${p.getName()}", ParameterDirection.Output).getValue();
#end
#end
		return (Integer)results.get(RET_CODE);
	}
#end
#if($host.getSpDelete().isExist() && $host.getSpDelete().getType()=="sp3" && $host.generateAPI(40))
	/**
	 * Batch SP delete without out parameters
	 * Return how many rows been affected for each of parameters
	 */
	public int[] delete(DalHints hints, ${host.getPojoClassName()}...daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.length == 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
##String callSql = client.buildCallSql(BATCH_DELETE_SP_NAME, parser.getFields(daoPojos[0]).size());
		String callSql = client.buildCallSql(BATCH_DELETE_SP_NAME, $host.getSpDelete().getParameters().size());
		StatementParameters[] parametersList = new StatementParameters[daoPojos.length];
		for(int i = 0; i< daoPojos.length; i++){
			StatementParameters parameters = new StatementParameters();
#foreach($p in $host.getSpDelete().getParameters())
		    parameters.set("${p.getName()}", ${p.getJavaTypeDisplay()}, daoPojos[i].get${p.getName()}());
#end
##client.addParametersByName(parameters, parser.getFields(daoPojos[i]));
			parametersList[i] = parameters;
		}
		return baseClient.batchCall(callSql, parametersList, hints);
	}
#end
#if($host.getSpDelete().isExist() && $host.getSpDelete().getType()=="sp3" && $host.generateAPI(94))
	/**
	 * Batch SP delete without out parameters
	 * Return how many rows been affected for each of parameters
	 */
	public int[] delete(DalHints hints, List<${host.getPojoClassName()}> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() == 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		String callSql = client.buildCallSql(BATCH_DELETE_SP_NAME, $host.getSpDelete().getParameters().size());
		StatementParameters[] parametersList = new StatementParameters[daoPojos.size()];
		for(int i = 0; i< daoPojos.size(); i++){
			StatementParameters parameters = new StatementParameters();
#foreach($p in $host.getSpDelete().getParameters())
		    parameters.set("${p.getName()}", ${p.getJavaTypeDisplay()}, daoPojos.get(i).get${p.getName()}());
#end
			parametersList[i] = parameters;
		}
		return baseClient.batchCall(callSql, parametersList, hints);
	}
#end