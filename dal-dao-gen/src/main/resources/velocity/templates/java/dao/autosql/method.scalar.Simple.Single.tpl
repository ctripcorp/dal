#foreach($method in $host.getMethods())
#if($method.getCrud_type() == "select")
##简单类型并且返回值为Single
#if($method.isSampleType() && $method.isReturnSingle())
	/**
	 * ${method.getComments()}
	**/
	public ${method.getPojoClassName()} ${method.getName()}(${method.getParameterDeclaration()}) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		SelectSqlBuilder builder = new SelectSqlBuilder("${method.getTableName()}", dbCategory, false);
		builder.select(${method.getField()});
#parse("templates/java/dao/autosql/common.statement.parameters.tpl")
	    String sql = builder.build();
		return queryDao.queryForObjectNullable(sql, builder.buildParameters(), hints, ${method.getPojoClassName()}.class);
	}
#end
#end
#end