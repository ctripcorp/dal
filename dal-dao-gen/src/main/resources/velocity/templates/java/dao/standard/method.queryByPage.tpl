#if($host.generateAPI(5,17))

	/**
	 * Query ${host.getPojoClassName()} with paging function
	 * The pageSize and pageNo must be greater than zero.
	**/
	public List<${host.getPojoClassName()}> queryByPage(int pageSize, int pageNo, DalHints hints)  throws SQLException {
		if(pageNo < 1 || pageSize < 1) 
			throw new SQLException("Illigal pagesize or pageNo, pls check");	
		hints = DalHints.createIfAbsent(hints);

		SelectSqlBuilder builder = new SelectSqlBuilder("${host.getPojoClassName()}", dbCategory);
		builder.selectAll();
		builder.atPage(pageNo, pageSize);
		builder.orderBy("${host.getOverColumns()}", ASC);

		return client.query(builder, hints);
	}
#end
