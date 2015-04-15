#foreach($method in $host.getMethods())
#if($method.getCrud_type()=="select")
##简单类型并且返回值是List
#if($method.isSampleType() && $method.isReturnList())
	/**
	 * ${method.getComments()}
	**/
	public List<${method.getPojoClassName()}> ${method.getName()}(${method.getParameterDeclaration()}) throws SQLException {	
#if($method.isPaging())
		String sql = "${method.getPagingSql($host.getDatabaseCategory())}";
#else
		String sql = "${method.getSql()}";
#end
#if($method.isInClauses())
		sql = SQLParser.parse(sql, ${method.getInClauses()});
#end
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
#if($method.hasParameters() || $method.isPaging())
		int i = 1;
#end		
#if($method.hasParameters())
#foreach($p in $method.getParameters())
#if($p.isInParameter())
		i = parameters.setInParameter(i, "${p.getAlias()}", ${p.getJavaTypeDisplay()}, ${p.getAlias()});
#else
		parameters.set(i++, "${p.getAlias()}", ${p.getJavaTypeDisplay()}, ${p.getName()});
#end
#end
#end
#if($method.isPaging())
		parameters.set(i++, Types.INTEGER, ${host.pageBegain()});
		parameters.set(i++, Types.INTEGER, ${host.pageEnd()});
#end
		return queryDao.query(sql, parameters, hints, ${method.getPojoClassName()}.class);
	}
#end
#end
#end