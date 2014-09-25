#foreach($method in $host.getMethods())
#if($method.getCrud_type() == "select")
##简单类型且返回值为First
#if($method.isSampleType() && $method.isReturnFirst())
	/**
	 * ${method.getComments()}
	**/
	public ${method.getPojoClassName()} ${method.getName()}(${method.getParameterDeclaration()}) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		SelectSqlBuilder builder = new SelectSqlBuilder("${method.getTableName()}");
		builder.addSelectField(${method.getField()});
		StatementParameters parameters = new StatementParameters();
		int index = 1;
#parse("templates/java/dao/autosql/common.statement.parameters.tpl")
#if($method.getOrderByExp()!="")
		builder.addOrderByExp("${method.getOrderByExp()}");
#end
	    String sql = builder.buildSelectSql();
		return queryDao.queryFirstNullable(sql, parameters, hints, ${method.getPojoClassName()}.class);
	}
#end
#end
#end