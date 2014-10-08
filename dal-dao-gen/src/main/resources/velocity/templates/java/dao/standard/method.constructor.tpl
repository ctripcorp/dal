##构造函数
	public ${host.getPojoClassName()}Dao() {
		this.client = new DalTableDao<${host.getPojoClassName()}>(parser);
		this.client.setDatabaseCategory(dbCategory);
#*
#if($host.getDatabaseCategory().name() == "MySql")
		this.client.setDelimiter('`','`');
#else
		this.client.setDelimiter('[',']');
		this.client.setFindTemplate("SELECT * FROM %s WITH (NOLOCK) WHERE %s");
#end
*#
#if($host.hasMethods())
		this.queryDao = new DalQueryDao(DATA_BASE);
#end
		this.rowextractor = new DalRowMapperExtractor<${host.getPojoClassName()}>(parser); 
		this.baseClient = DalClientFactory.getClient(DATA_BASE);
	}
