#foreach($method in $host.getMethods())
#if($method.getCrud_type() == "delete" )
	/**
	 * ${method.getComments()}
	**/
	public int ${method.getName()} (${method.getParameterDeclaration()}) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		
		DeleteSqlBuilder builder = new DeleteSqlBuilder("${method.getTableName()}");
		
		StatementParameters parameters = new StatementParameters();
		int index = 1;
#parse("templates/java/dao/autosql/common.statement.parameters.tpl")
	    String sql = builder.buildDelectSql();
		
		return baseClient.update(sql, parameters, hints);
	}
#end
#end