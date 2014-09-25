#foreach($method in $host.getMethods())
#if($method.getCrud_type() == "select")
##实体类型并且返回值为List
#if($method.isReturnList() && !$method.isSampleType())
	/**
	 * ${method.getComments()}
	**/
	public List<${host.getPojoClassName()}> ${method.getName()}(${method.getParameterDeclaration()}) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		SelectSqlBuilder builder = new SelectSqlBuilder("${method.getTableName()}");
		builder.addSelectField(${method.getField()});
		StatementParameters parameters = new StatementParameters();
		int index = 1;
#parse("templates/java/dao/autosql/common.statement.parameters.tpl")
#if($method.getOrderByExp()!="")
		builder.addOrderByExp("${method.getOrderByExp()}");
#end
#if($method.isPaging() && ${host.getDatabaseCategory()}=="MySql")
		String sqlPattern = builder.buildPaginationSql4MySQL();
		String sql = String.format(sqlPattern, ${host.pageBegain()}, ${host.pageEnd()});
#end
#if($method.isPaging() && ${host.getDatabaseCategory()}!="MySql")
		String sqlPattern = builder.buildPaginationSql4SqlServer();
		String sql = String.format(sqlPattern, ${host.pageBegain()}, ${host.pageEnd()});
#end
#if(!$method.isPaging())
		String sql = builder.buildSelectSql();
#end
		return queryDao.query(sql, parameters, hints, parser);
	}
#end
#end
#end