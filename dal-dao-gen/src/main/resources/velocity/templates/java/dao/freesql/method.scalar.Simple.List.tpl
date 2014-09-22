#foreach($method in $host.getMethods())
#if($method.getCrud_type()=="select")
##简单类型并且返回值是List
#if($method.isSampleType() && $method.isReturnList())
	/**
	 * ${method.getComments()}
	**/
	public List<${method.getPojoClassName()}> ${method.getName()}(${method.getParameterDeclaration()}) throws SQLException {	
#if($method.isPaging())
		String sqlPattern = "${method.getPagingSql($host.getDatabaseCategory())}";
		String sql = String.format(sqlPattern, ${host.pageBegain()}, ${host.pageEnd()});
#else
		String sql = "${method.getSql()}";
#end
#if($method.isInClauses())
		sql = SQLParser.parse(sql, ${method.getInClauses()});
#end
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
#if($method.hasParameters())
		int i = 1;
#foreach($p in $method.getParameters())
#if($p.isInParameter())
		i = parameters.setInParameter(i, ${p.getJavaTypeDisplay()}, ${p.getAlias()});
#else
		parameters.set(i++, ${p.getJavaTypeDisplay()}, ${p.getName()});
#end
#end
#end
		return queryDao.query(sql, parameters, hints, ${method.getPojoClassName()}.class);
	}
#end
#end
#end