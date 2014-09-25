#foreach($method in $host.getMethods())
#if($method.getCrud_type() == "select")
##简单类型并且返回值为Single
#if($method.isSampleType() && $method.isReturnSingle())
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
	    String sql = builder.buildSelectSql();
		return queryDao.queryForObjectNullable(sql, parameters, hints, ${method.getPojoClassName()}.class);
	}
#end
#end
#end