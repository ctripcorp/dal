#foreach($method in $host.getMethods())
#if($method.getCrud_type() == "select")
##实体类型且返回First
#if($method.isReturnFirst() && !$method.isSampleType())
	/**
	 * ${method.getComments()}
	**/
	public ${host.getPojoClassName()} ${method.getName()}(${method.getParameterDeclaration()}) throws SQLException {
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
		return queryDao.queryFirstNullable(sql, parameters, hints, parser);
	}
#end
#end
#end