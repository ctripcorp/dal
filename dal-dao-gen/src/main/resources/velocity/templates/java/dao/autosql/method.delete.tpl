#foreach($method in $host.getMethods())
#if($method.getCrud_type() == "delete" )
	/**
	 * ${method.getComments()}
	**/
	public int ${method.getName()} (${method.getParameterDeclaration()}) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
#parse("templates/java/Hints.java.tpl")
		DeleteSqlBuilder builder = new DeleteSqlBuilder("${method.getTableName()}", dbCategory);
#parse("templates/java/dao/autosql/common.statement.parameters.tpl")
	    String sql = builder.build();
		return client.update(sql, builder.buildParameters(), hints);
	}
#end
#end