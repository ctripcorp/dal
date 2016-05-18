#if($host.generateAPI(6,18))

	/**
	 * Get all records in the whole table
	**/
	public List<${host.getPojoClassName()}> getAll(DalHints hints) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		return client.query(QUERY_ALL_CRITERIA, parameters, hints);
	}
#end