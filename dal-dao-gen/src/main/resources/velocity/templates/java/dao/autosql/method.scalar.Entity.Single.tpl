#foreach($method in $host.getMethods())
#if($method.getCrud_type() == "select")
##实体类型且返回Single
#if($method.isReturnSingle() && !$method.isSampleType())
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
	    String sql = builder.buildSelectSql();
		return queryDao.queryForObjectNullable(sql, parameters, hints, parser);
	}
#end
#end
#end