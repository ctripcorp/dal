#if($host.isSp() && ($host.getSpInsert().isExist() || $host.getSpDelete().isExist() ||$host.getSpUpdate().isExist()))
	private String prepareSpCall(String SpName, StatementParameters parameters, Map<String, ?> fields) {
		client.addParametersByName(parameters, fields);
		String callSql = client.buildCallSql(SpName, fields.size());
		parameters.setResultsParameter(RET_CODE, extractor);
		return callSql;
	}
#end