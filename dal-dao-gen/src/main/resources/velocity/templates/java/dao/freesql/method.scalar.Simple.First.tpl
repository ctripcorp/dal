#foreach($method in $host.getMethods())
#if($method.getCrud_type()=="select")
##简单类型且返回值为First
#if($method.isSampleType() && $method.isReturnFirst())
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
		return queryDao.queryFirstNullable(sql, parameters, hints, ${method.getPojoClassName()}.class);
	}
#end
#end
#end


