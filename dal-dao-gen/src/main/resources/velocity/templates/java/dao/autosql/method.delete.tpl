#foreach($method in $host.getMethods())
#if($method.getCrud_type() == "delete" )
	/**
	 * ${method.getComments()}
	**/
	public int ${method.getName()} (${method.getParameterDeclaration()}) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		DeleteSqlBuilder builder = new DeleteSqlBuilder("${method.getTableName()}", dBCategory);
#parse("templates/java/dao/autosql/common.statement.parameters.tpl")
	    String sql = builder.build();
		return baseClient.update(sql, builder.buildParameters, hints);
	}
#end
#end