#foreach($method in $host.getMethods())
#if($method.getCrud_type()=="select")
##简单类型并且返回值为Single
#if($method.isSampleType() && $method.isReturnSingle())
	/**
	 * ${method.getComments()}
	**/
	public ${method.getPojoClassName()} ${method.getName()}(${method.getParameterDeclaration()}) throws SQLException {
		String sql = "${method.getSql()}";
#if($method.isInClauses())
		sql = SQLParser.parse(sql, ${method.getInClauses()});
#end
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
#if($method.isAllShard())
		hints.inAllShards();
#end
#if($method.isShards())
		hints.inShards(shards);
#end
#if($method.isAsync())
		hints.asyncExecution();
#end
#if($method.isCallback())
		hints.callbackWith(callback);
#end
#if($method.hasParameters())
		int i = 1;
#foreach($p in $method.getParameters())
#set($sensitiveflag = "")	
#if(${p.isSensitive()})
#set($sensitiveflag = "Sensitive")
#end	
#if($p.isInParameter())
		i = parameters.set$!{sensitiveflag}InParameter(i, "${p.getAlias()}", ${p.getJavaTypeDisplay()}, ${p.getAlias()});
#else
		parameters.set$!{sensitiveflag}(i++, "${p.getAlias()}", ${p.getJavaTypeDisplay()}, ${p.getName()});
#end
#end
#end
		return queryDao.queryForObjectNullable(sql, parameters, hints, ${method.getPojoClassName()}.class);
	}
#end
#end
#end