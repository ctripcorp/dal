#if(!$host.getSpUpdate().isExist() && $host.generateAPI(12,33))
	/**
	 * SQL update
	 * Note: there must be one non-null field in daoPojo
	**/
	public int update(DalHints hints, ${host.getPojoClassName()}...daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.length <= 0)
			return 0;
		hints = DalHints.createIfAbsent(hints);
		return client.update(hints, daoPojos);
	}
#end
#if(!$host.getSpUpdate().isExist() && $host.generateAPI(90,91))
	/**
	 * SQL update
	 * Note: there must be one non-null field in daoPojo
	**/
	public int update(DalHints hints, List<${host.getPojoClassName()}> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.size() <= 0)
			return 0;
		hints = DalHints.createIfAbsent(hints);
		return client.update(hints, daoPojos);
	}
#end