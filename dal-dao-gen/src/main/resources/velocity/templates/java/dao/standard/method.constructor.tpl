##构造函数
	public ${host.getPojoClassName()}Dao() throws SQLException {
		this.client = new DalTableDao<>(new DalDefaultJpaParser<>(${host.getPojoClassName()}.class));
	}
