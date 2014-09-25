#foreach($method in $host.getMethods())
#if($method.getCrud_type() == "update")
	/**
	 * ${method.getComments()}
	**/
	public int ${method.getName()} (${method.getUpdateParameterDeclaration()}) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		UpdateSqlBuilder builder = new UpdateSqlBuilder("${method.getTableName()}");
		builder.addUpdateField(${method.getField()});
		StatementParameters parameters = new StatementParameters();
		int index = 1;
#foreach($p in $method.getUpdateSetParameters())
		parameters.set(index++, ${p.getJavaTypeDisplay()}, ${p.getAlias()});
#end	
#parse("templates/java/dao/autosql/common.statement.parameters.tpl")
		String sql = builder.buildUpdateSql();
		return baseClient.update(sql, parameters, hints);
	}
#end
#end