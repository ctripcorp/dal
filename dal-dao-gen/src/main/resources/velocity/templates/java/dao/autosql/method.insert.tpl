#foreach($method in $host.getMethods())
#if($method.getCrud_type() == "insert")
	/**
	 * ${method.getComments()}
	**/
	public int ${method.getName()} (${method.getParameterDeclaration()}) throws SQLException {
#if(${method.getInClauses()} != "")
		String sql = SQLParser.parse("${method.getSql()}",${method.getInClauses()});
#else
		String sql = SQLParser.parse("${method.getSql()}");
#end
		StatementParameters parameters = new StatementParameters();
		hints = DalHints.createIfAbsent(hints);
#parse("templates/java/Hints.java.tpl")
		int i = 1;
#foreach($p in $method.getParameters())
#set($sensitiveflag = "")	
#if(${p.isSensitive()})
#set($sensitiveflag = "Sensitive")
#end
#if($p.isInParameter())
		i = parameters.set$!{sensitiveflag}InParameter(i, "${p.getAlias()}", ${p.getJavaTypeDisplay()}, ${p.getAlias()});	
#else
		parameters.set$!{sensitiveflag}(i++, "${p.getAlias()}", ${p.getJavaTypeDisplay()}, ${p.getAlias()});
#end
#end
		return client.update(sql, parameters, hints);
	}
#end
#end