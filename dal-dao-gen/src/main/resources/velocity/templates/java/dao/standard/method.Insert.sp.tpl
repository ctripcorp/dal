#if($host.getSpInsert().isExist())
#if($host.generateAPI(19))
	/**
	 * SP Insert
	**/
	public int insert(DalHints hints, ${host.getPojoClassName()} daoPojo) throws SQLException {
		if(null == daoPojo)
			return 0;
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		String callSql = prepareSpCall(INSERT_SP_NAME, parameters, parser.getFields(daoPojo));
		Map<String, ?> results = baseClient.call(callSql, parameters, hints);
		return (Integer)results.get(RET_CODE);
	}
#end
#if($host.generateAPI(73))	
	public int insert(DalHints hints, KeyHolder holder, ${host.getPojoClassName()} daoPojo) throws SQLException {
		if(null == daoPojo)
			return 0;
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		String callSql = prepareSpCall(INSERT_SP_NAME, parameters, parser.getFields(daoPojo));
#foreach($p in $host.getSpInsert().getParameters())
#if($p.getDirection().name() == "InputOutput")
		parameters.registerInOut("${p.getName()}", ${p.getJavaTypeDisplay()}, daoPojo.get${p.getCapitalizedName()}());
#end
#if($p.getDirection().name() == "Output")
		parameters.registerOut("${p.getName()}", ${p.getJavaTypeDisplay()});
#end
#end
		Map<String, ?> results = baseClient.call(callSql, parameters, hints);
		
		if(holder != null){
			Map<String, Object> map = new LinkedHashMap<String, Object>();
#foreach($p in $host.getSpInsert().getParameters())
#if($p.getDirection().name() == "InputOutput")
		    map.put("${p.getName()}", parameters.get("${p.getName()}", ParameterDirection.InputOutput).getValue());
#end
#if($p.getDirection().name() == "Output")
		    map.put("${p.getName()}", parameters.get("${p.getName()}", ParameterDirection.InputOutput).getValue())
#end
#end
	        holder.getKeyList().add(map);
		}
		return (Integer)results.get(RET_CODE);
	}
#end

#if($host.getSpInsert().getType()=="sp3" && $host.generateAPI(39))
	/**
	 * Batch insert without out parameters
	 * Return how many rows been affected for each of parameters
	**/
	public int[] insert(DalHints hints, ${host.getPojoClassName()}...daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.length == 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		String callSql = client.buildCallSql(INSERT_SP_NAME, parser.getFields(daoPojos[0]).size());
		StatementParameters[] parametersList = new StatementParameters[daoPojos.length];
		for(int i = 0; i< daoPojos.length; i++){
			StatementParameters parameters = new StatementParameters();
			client.addParametersByName(parameters, parser.getFields(daoPojos[i]));
			parametersList[i] = parameters;
		}
		return baseClient.batchCall(callSql, parametersList, hints);
	}
#end
#end