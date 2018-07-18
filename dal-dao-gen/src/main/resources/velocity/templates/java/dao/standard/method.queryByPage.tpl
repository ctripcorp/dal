#if($host.generateAPI(5,17))

	/**
	 * Query ${host.getPojoClassName()} with paging function
	 * The pageSize and pageNo must be greater than zero.
	 */
	public List<${host.getPojoClassName()}> queryAllByPage(int pageNo, int pageSize)  throws SQLException {
		return queryAllByPage(pageNo, pageSize, null);
	}

	/**
	 * Query ${host.getPojoClassName()} with paging function
	 * The pageSize and pageNo must be greater than zero.
	 */
	public List<${host.getPojoClassName()}> queryAllByPage(int pageNo, int pageSize, DalHints hints)  throws SQLException {
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.selectAll().atPage(pageNo, pageSize).orderBy("${host.getOverColumns()}", ASC);

		return client.query(builder, hints);
	}
#end
