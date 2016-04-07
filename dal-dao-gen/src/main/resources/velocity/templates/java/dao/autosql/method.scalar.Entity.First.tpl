#foreach($method in $host.getMethods())
#if($method.getCrud_type() == "select")
##实体类型且返回First
#if($method.isReturnFirst() && !$method.isSampleType())
	/**
	 * ${method.getComments()}
	**/
	public ${host.getPojoClassName()} ${method.getName()}(${method.getParameterDeclaration()}) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
#parse("templates/java/Hints.java.tpl")
		SelectSqlBuilder builder = new SelectSqlBuilder("${method.getTableName()}", dbCategory, false);
		builder.select(${method.getField()});
#parse("templates/java/dao/autosql/common.statement.parameters.tpl")
#if($method.getOrderByExp()!="")
		builder.orderBy(${method.getOrderByExp()});
#end
	    String sql = builder.buildFirst();
		return queryDao.queryFirstNullable(sql, builder.buildParameters(), hints, parser);
	}
#end
#end
#end