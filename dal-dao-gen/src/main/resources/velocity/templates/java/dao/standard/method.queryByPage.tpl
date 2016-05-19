#if($host.generateAPI(5,17))

	/**
	 * Because the pageSize and pageNo is not defined in an order that complies with 
	 * other query by page method, it is deprecated. It will be remove in near release
	 * @deprecated please use method queryAllByPage() instead
	 */
	public List<${host.getPojoClassName()}> queryByPage(int pageSize, int pageNo, DalHints hints)  throws SQLException {
		return queryAllByPage(pageNo, pageSize, hints);
	}
	
	/**
	 * Query ${host.getPojoClassName()} with paging function
	 * The pageSize and pageNo must be greater than zero.
	 */
	public List<${host.getPojoClassName()}> queryAllByPage(int pageNo, int pageSize, DalHints hints)  throws SQLException {
		if(pageNo < 1 || pageSize < 1) 
			throw new SQLException("Illigal pagesize or pageNo, pls check");	
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder(parser.getTableName(), dbCategory);
		builder.selectAll();
		builder.atPage(pageNo, pageSize);
		builder.orderBy("${host.getOverColumns()}", ASC);

		return client.query(builder, hints);
	}
#end
