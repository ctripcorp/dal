#if($host.generateAPI(4,16))

	/**
	 * Get the records count
	**/
	public int count(DalHints hints) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		Number result = (Number)client.count(QUERY_ALL_CRITERIA, parameters, hints);
		return result.intValue();
	}
#end