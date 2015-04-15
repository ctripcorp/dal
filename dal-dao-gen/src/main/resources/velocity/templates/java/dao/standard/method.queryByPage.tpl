#if($host.generateAPI(5,17))
	/**
	 * Query ${host.getPojoClassName()} with paging function
	 * The pageSize and pageNo must be greater than zero.
	**/
	public List<${host.getPojoClassName()}> queryByPage(int pageSize, int pageNo, DalHints hints)  throws SQLException {
		if(pageNo < 1 || pageSize < 1) 
			throw new SQLException("Illigal pagesize or pageNo, pls check");	
        StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
#if($host.getDatabaseCategory().name() == "MySql" )
		String sql = PAGE_MYSQL_PATTERN;
		parameters.set(1, Types.INTEGER, (pageNo - 1) * pageSize);
		parameters.set(2, Types.INTEGER, pageSize);
#else
		String sql = PAGE_SQL_PATTERN;
		int fromRownum = (pageNo - 1) * pageSize + 1;
        int endRownum = pageSize * pageNo;
		parameters.set(1, Types.INTEGER, fromRownum);
		parameters.set(2, Types.INTEGER, endRownum);
#end
		return this.baseClient.query(sql, parameters, hints, rowextractor);
	}
#end
