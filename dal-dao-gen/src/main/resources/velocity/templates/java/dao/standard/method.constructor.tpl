##构造函数
	public ${host.getPojoClassName()}Dao() throws SQLException {
		parser = new DalDefaultJpaParser<>(${host.getPojoClassName()}.class);
		this.client = new DalTableDao<${host.getPojoClassName()}>(parser);
		dbCategory = this.client.getDatabaseCategory();
	}
