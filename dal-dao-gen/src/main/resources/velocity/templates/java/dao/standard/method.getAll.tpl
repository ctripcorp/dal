#if($host.generateAPI(6,18,27))
	/**
	 * Get all records in the whole table
	**/
	public List<${host.getPojoClassName()}> getAll(DalHints hints) throws SQLException {
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		List<${host.getPojoClassName()}> result = null;
		result = this.baseClient.query(ALL_SQL_PATTERN, parameters, hints, rowextractor);
		return result;
	}
#end