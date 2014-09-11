#foreach($method in $host.getMethods())
	/**
	 * ${method.getComments()}
	**/
#if($method.getCrud_type() == "select")
##简单类型并且返回值是List
#if($method.isSampleType() && $method.isReturnList())
	public List<${method.getPojoClassName()}> ${method.getName()}(${method.getParameterDeclaration()}) throws SQLException {
#if($method.isPaging())
		String sqlPattern = "${method.getPagingSql($host.getDatabaseCategory())}";
		String sql = String.format(sqlPattern, (pageNo - 1) * pageSize + 1, pageSize * pageNo);
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
		parameters.set(i++, ${p.getJavaTypeDisplay()}, ${p.getAlias()});
#end
#end
#end
		return queryDao.query(sql, parameters, hints, ${method.getPojoClassName()}.class);
	}
#end
##简单类型并且返回值为Single
#if($method.isSampleType() && $method.isReturnSingle())
	public ${method.getPojoClassName()} ${method.getName()}(${method.getParameterDeclaration()}) throws SQLException {
		String sql = "${method.getSql()}";
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
		parameters.set(i++, ${p.getJavaTypeDisplay()}, ${p.getAlias()});
#end
#end
#end
		return queryDao.queryForObjectNullable(sql, parameters, hints, ${method.getPojoClassName()}.class);
	}
#end
##简单类型且返回值为First
#if($method.isSampleType() && $method.isReturnFirst())
	public ${method.getPojoClassName()} ${method.getName()}(${method.getParameterDeclaration()}) throws SQLException {
		String sql = "${method.getSql()}";
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
		parameters.set(i++, ${p.getJavaTypeDisplay()}, ${p.getAlias()});
#end
#end
#end
		return queryDao.queryFirstNullable(sql, parameters, hints, ${method.getPojoClassName()}.class);
	}
#end
##实体类型并且返回值为List
#if($method.isReturnList() && !$method.isSampleType())
	public List<${host.getPojoClassName()}> ${method.getName()}(${method.getParameterDeclaration()}) throws SQLException {
#if($method.isPaging())
		String sqlPattern = "${method.getPagingSql($host.getDatabaseCategory())}";
		String sql = String.format(sqlPattern, (pageNo - 1) * pageSize + 1, pageSize * pageNo);
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
		parameters.set(i++, ${p.getJavaTypeDisplay()}, ${p.getAlias()});
#end
#end
#end
		return queryDao.query(sql, parameters, hints, parser);
	}
#end
##实体类型且返回Signle
#if($method.isReturnSingle() && !$method.isSampleType())
	public ${host.getPojoClassName()} ${method.getName()}(${method.getParameterDeclaration()}) throws SQLException {
		String sql = "${method.getSql()}";
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
		parameters.set(i++, ${p.getJavaTypeDisplay()}, ${p.getAlias()});
#end
#end
#end
		return queryDao.queryForObjectNullable(sql, parameters, hints, parser);
	}
#end
##实体类型且返回First
#if($method.isReturnFirst() && !$method.isSampleType())
	public ${host.getPojoClassName()} ${method.getName()}(${method.getParameterDeclaration()}) throws SQLException {
		String sql = "${method.getSql()}";
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
		parameters.set(i++, ${p.getJavaTypeDisplay()}, ${p.getAlias()});
#end
#end
#end
		return queryDao.queryFirstNullable(sql, parameters, hints, parser);
	}
#end
#else
	public int ${method.getName()}(${method.getParameterDeclaration()}) throws SQLException {
#if(${method.getInClauses()} != "")
		String sql = SQLParser.parse("${method.getSql()}",${method.getInClauses()});
#else
		String sql = SQLParser.parse("${method.getSql()}");
#end
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
		
		int i = 1;
#foreach($p in $method.getParameters())
#if($p.isInParameter())
		i = parameters.setInParameter(i, ${p.getJavaTypeDisplay()}, ${p.getAlias()});
#else
		parameters.set(i++, ${p.getJavaTypeDisplay()}, ${p.getAlias()});
#end
#end
		return baseClient.update(sql, parameters, hints);
	}
#end
#end
