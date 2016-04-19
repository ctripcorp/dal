#foreach($method in $host.getMethods())
#if($method.getCrud_type() == "select")
##实体类型且返回Single
#if($method.isReturnSingle() && !$method.isSampleType())

	/**
	 * ${method.getComments()}
	**/
	public ${host.getPojoClassName()} ${method.getName()}(${method.getParameterDeclaration()}) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
#parse("templates/java/Hints.java.tpl")

		SelectSqlBuilder builder = new SelectSqlBuilder("${method.getTableName()}", dbCategory);
		builder.select(${method.getField()});
#parse("templates/java/dao/autosql/common.statement.parameters.tpl")

		return client.queryObject(builder, hints);
	}
#end
#end
#end