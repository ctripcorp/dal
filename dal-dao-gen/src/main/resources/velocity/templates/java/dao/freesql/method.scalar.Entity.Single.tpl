#foreach($method in $host.getMethods())
#if($method.getCrud_type()=="select")
##实体类型且返回Single
#if($method.isReturnSingle() && !$method.isSampleType())
	/**
	 * ${method.getComments()}
	**/
	public ${method.getPojoClassName()} ${method.getName()}(${method.getParameterDeclaration()}) throws SQLException {
		String sql = "${method.getSql()}";
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
#if($method.hasParameters())
		int i = 1;
#foreach($p in $method.getParameters())
		parameters.set(i++, ${p.getJavaTypeDisplay()}, ${p.getName()});
#end
#end
		return queryDao.queryForObjectNullable(sql, parameters, hints, ${method.getVariableName()}RowMapper);
	}
#end
#end
#end