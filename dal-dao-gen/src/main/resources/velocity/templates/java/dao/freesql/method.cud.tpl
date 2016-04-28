#foreach($method in $host.getMethods())
#if($method.getCrud_type() != "select")
	/**
	 * ${method.getComments()}
	**/
	public int ${method.getName()} (${method.getParameterDeclaration()}) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
#parse("templates/java/Hints.java.tpl")

		String sql = "${method.getSql()}";
		StatementParameters parameters = new StatementParameters();
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

		return queryDao.update(sql, parameters, hints);
	}
#end
#end