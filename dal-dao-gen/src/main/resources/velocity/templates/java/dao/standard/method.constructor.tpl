##构造函数
	public ${host.getPojoClassName()}Dao() throws SQLException {
		parser = DalDefaultJpaParser.create(${host.getPojoClassName()}.class, DATA_BASE);
		this.client = new DalTableDao<${host.getPojoClassName()}>(parser);
		dbCategory = this.client.getDatabaseCategory();
#if($host.hasMethods())
		this.queryDao = new DalQueryDao(DATA_BASE);
#end
		this.rowextractor = new DalRowMapperExtractor<${host.getPojoClassName()}>(parser); 
		this.baseClient = DalClientFactory.getClient(DATA_BASE);
	}
