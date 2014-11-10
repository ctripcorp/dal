#if($host.getSpUpdate().isExist() && $host.generateAPI(21))
	/**
	 * SP update
	**/
	public int update(DalHints hints, ${host.getPojoClassName()} daoPojo) throws SQLException {
		if(null == daoPojo)
			return 0;
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		String callSql = prepareSpCall(BASIC_UPDATE_SP_NAME, parameters, parser.getFields(daoPojo));
#foreach($p in $host.getSpUpdate().getParameters())
#if($p.getDirection().name() == "InputOutput")
		parameters.registerInOut("${p.getName()}", ${p.getJavaTypeDisplay()}, daoPojo.get${p.getCapitalizedName()}());
#end
#if($p.getDirection().name() == "Output")
		parameters.registerOut("${p.getName()}", ${p.getJavaTypeDisplay()});
#end
#end
		Map<String, ?> results = baseClient.call(callSql, parameters, hints);
#foreach($p in $host.getSpUpdate().getParameters())
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
#if($host.getSpUpdate().isExist() && $host.getSpUpdate().getType()=="sp3" && $host.generateAPI(92)) 
	/**
	 * Batch SP update without out parameters
	 * Return how many rows been affected for each of parameters
	 */
	public int[] update(DalHints hints, ${host.getPojoClassName()}... daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.length == 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		String callSql = client.buildCallSql(BATCH_UPDATE_SP_NAME, parser.getFields(daoPojos[0]).size());
		StatementParameters[] parametersList = new StatementParameters[daoPojos.length];
		for(int i = 0; i< daoPojos.length; i++){
			StatementParameters parameters = new StatementParameters();
			client.addParametersByName(parameters, parser.getFields(daoPojos[i]));
			parametersList[i] = parameters;
		}
		return baseClient.batchCall(callSql, parametersList, hints);
	}
#end
#if($host.getSpUpdate().isExist() && $host.getSpUpdate().getType()=="sp3" && $host.generateAPI(95)) 
	/**
	 * Batch SP update without out parameters
	 * Return how many rows been affected for each of parameters
	 */
	public int[] update(DalHints hints, List<${host.getPojoClassName()}> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() == 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		String callSql = client.buildCallSql(BATCH_UPDATE_SP_NAME, parser.getFields(daoPojos.get(0)).size());
		StatementParameters[] parametersList = new StatementParameters[daoPojos.size()];
		for(int i = 0; i< daoPojos.size(); i++){
			StatementParameters parameters = new StatementParameters();
			client.addParametersByName(parameters, parser.getFields(daoPojos.get(i)));
			parametersList[i] = parameters;
		}
		return baseClient.batchCall(callSql, parametersList, hints);
	}
#end