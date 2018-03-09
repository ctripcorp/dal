#if($host.generateAPI(6,18))

	/**
	 * Get all records from table
	 */
	public List<${host.getPojoClassName()}> queryAll() throws SQLException {
		return queryAll(null);
	}

	/**
	 * Get all records from table
	 */
	public List<${host.getPojoClassName()}> queryAll(DalHints hints) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		
		SelectSqlBuilder builder = new SelectSqlBuilder().selectAll().orderBy("${host.getOverColumns()}", ASC);
		
		return client.query(builder, hints);
	}
#end