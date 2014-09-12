#if(!$host.getSpDelete().isExist() && $host.generateAPI(10,31))
	/**
	 * SQL delete
	 * Note: there must be one non-null field in daoPojo
	**/
	public int delete(DalHints hints, ${host.getPojoClassName()}...daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.length <= 0)
			return;
		hints = DalHints.createIfAbsent(hints);
		return client.delete(hints, daoPojos);
	}
#end
#if(!$host.getSpDelete().isExist() && $host.generateAPI(86,87))
	/**
	 * SQL delete
	 * Note: there must be one non-null field in daoPojo
	**/
	public int delete(DalHints hints, List<${host.getPojoClassName()}> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.length <= 0)
			return;
		hints = DalHints.createIfAbsent(hints);
		return client.delete(hints, daoPojos);
	}
#end
#if(!$host.getSpDelete().isExist() && $host.generateAPI(11,32))
	/**
	 * SQL delete with batch mode
	**/
	public int[] batchDelete(DalHints hints, ${host.getPojoClassName()}...daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.length <= 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.batchDelete(hints, daoPojos);
	}
#end
#if(!$host.getSpDelete().isExist() && $host.generateAPI(88,89))
	/**
	 * SQL delete with batch mode
	**/
	public int[] batchDelete(DalHints hints, List<${host.getPojoClassName()}> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.length <= 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.batchDelete(hints, daoPojos);
	}
#end