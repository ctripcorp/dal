#if(!$host.getSpUpdate().isExist())
#if($host.generateAPI(12,33))
	/**
	 * SQL update
	 * Note: there must be one non-null field in daoPojo
	**/
	public void update(DalHints hints, ${host.getPojoClassName()}...daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.length <= 0)
			return;
		hints = DalHints.createIfAbsent(hints);
		client.update(hints, daoPojos);
	}
#end
#end
