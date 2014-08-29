#if(!$host.getSpDelete().isExist())
#if($host.generateAPI(10,31))
	/**
	 * SQL delete
	 * Note: there must be one non-null field in daoPojo
	**/
	public void delete(DalHints hints, ${host.getPojoClassName()}...daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.length <= 0)
			return;
		hints = DalHints.createIfAbsent(hints);
		client.delete(hints, daoPojos);
	}
#end
#if($host.generateAPI(11,32))
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
#end
