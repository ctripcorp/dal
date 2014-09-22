#if(!$host.getSpInsert().isExist() && $host.generateAPI(7,28))
	/**
	 * SQL insert
	 * Note: there must be one non-null field in daoPojo
	**/
	public int insert(DalHints hints, ${host.getPojoClassName()}...daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.length <= 0)
			return 0;
		hints = DalHints.createIfAbsent(hints);
		return client.insert(hints, null, daoPojos);
	}
#end
#if(!$host.getSpInsert().isExist() && $host.generateAPI(75,77))
	/**
	 * SQL insert
	 * Note: there must be one non-null field in daoPojo
	**/
	public int insert(DalHints hints, List<${host.getPojoClassName()}> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.length <= 0)
			return 0;
		hints = DalHints.createIfAbsent(hints);
		return client.insert(hints, daoPojos);
	}
#end
#if(!$host.getSpInsert().isExist() && $host.generateAPI(9,30))
	/**
	 * SQL insert with keyHolder
	 * Note: there must be one non-null field in daoPojos
	**/
	public int insert(DalHints hints, KeyHolder keyHolder, ${host.getPojoClassName()}...daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.length <= 0)
			return 0;
		hints = DalHints.createIfAbsent(hints);
		return client.insert(hints, keyHolder, daoPojos);
	}
#end
#if(!$host.getSpInsert().isExist() && $host.generateAPI(78,79))
	/**
	 * SQL insert with keyHolder
	 * Note: there must be one non-null field in daoPojos
	**/
	public int insert(DalHints hints, KeyHolder keyHolder, List<${host.getPojoClassName()}> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.length <= 0)
			return 0;
		hints = DalHints.createIfAbsent(hints);
		return client.insert(hints, keyHolder, daoPojos);
	}
#end
#if(!$host.getSpInsert().isExist() && $host.generateAPI(8,29))
	/**
	 * SQL insert with batch mode
	**/
	public int[] batchInsert(DalHints hints, ${host.getPojoClassName()}...daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.length == 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.batchInsert(hints, daoPojos);
	}
#end
#if(!$host.getSpInsert().isExist() && $host.generateAPI(80,81))
	/**
	 * SQL insert with batch mode
	**/
	public int[] batchInsert(DalHints hints, List<${host.getPojoClassName()}> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.length == 0)
			return new int[0];
		hints = DalHints.createIfAbsent(hints);
		return client.batchInsert(hints, daoPojos);
	}
#end
#if(!$host.getSpInsert().isExist() && $host.generateAPI(82,83))
	/**
	 * SQL insert with batch mode
	 **/
	public int combinedInsert(DalHints hints, KeyHolder keyHolder, ${host.getPojoClassName()}... daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.length <= 0)
			return 0;
		hints = DalHints.createIfAbsent(hints);
		return client.combinedInsert(hints, keyHolder, daoPojos);
	}
#end
#if(!$host.getSpInsert().isExist() && $host.generateAPI(84,85))
	/**
	 * SQL insert with batch mode
	 **/
	public int combinedInsert(DalHints hints, KeyHolder keyHolder, List<${host.getPojoClassName()}> daoPojos) throws SQLException {
		if(null == daoPojos || daoPojos.length <= 0)
			return 0;
		hints = DalHints.createIfAbsent(hints);
		return client.combinedInsert(hints, keyHolder, daoPojos);
	}
#end