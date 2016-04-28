#foreach($method in $host.getMethods())
#if($method.getCrud_type()=="select")
##简单类型并且返回值为Single
#if($method.isSampleType() && $method.isReturnSingle())
	/**
	 * ${method.getComments()}
	**/
	public ${method.getPojoClassName()} ${method.getName()}(${method.getParameterDeclaration()}) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
#parse("templates/java/Hints.java.tpl")

		String sql = "${method.getSql()}";
		StatementParameters parameters = new StatementParameters();
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