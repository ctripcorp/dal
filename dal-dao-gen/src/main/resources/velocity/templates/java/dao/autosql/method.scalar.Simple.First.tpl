#foreach($method in $host.getMethods())
#if($method.getCrud_type() == "select")
##简单类型且返回值为First
#if($method.isSampleType() && $method.isReturnFirst())
	/**
	 * ${method.getComments()}
	**/
	public ${method.getPojoClassName()} ${method.getName()}(${method.getParameterDeclaration()}) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
#parse("templates/java/Hints.java.tpl")
		SelectSqlBuilder builder = new SelectSqlBuilder("${method.getTableName()}", dbCategory, false);
		builder.select(${method.getField()});
#parse("templates/java/dao/autosql/common.statement.parameters.tpl")
#if($method.getOrderByExp()!="")
		builder.orderBy(${method.getOrderByExp()});
#end
	    String sql = builder.buildFirst();
		return queryDao.queryFirstNullable(sql, builder.buildParameters(), hints, ${method.getPojoClassName()}.class);
	}
#end
#end
#end