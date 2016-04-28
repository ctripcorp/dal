#foreach($method in $host.getMethods())
#if($method.getCrud_type()=="select")
##实体类型并且返回值为List
#if($method.isReturnList() && !$method.isSampleType())
	/**
	 * ${method.getComments()}
	**/
	public List<${method.getPojoClassName()}> ${method.getName()}(${method.getParameterDeclaration()}) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
#parse("templates/java/Hints.java.tpl")

#if($method.isPaging())
		String sql = "${method.getPagingSql($host.getDatabaseCategory())}";
#else
		String sql = "${method.getSql()}";
#end
		StatementParameters parameters = new StatementParameters();
#if($method.hasParameters() || $method.isPaging())
		int i = 1;
#end
#if($method.hasParameters())
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
#if($method.isPaging())
		parameters.set(i++, Types.INTEGER, ${host.pageBegain()});
		parameters.set(i++, Types.INTEGER, ${host.pageEnd()});
#end

		return (List<${method.getPojoClassName()}>)queryDao.query(sql, parameters, hints, ${method.getVariableName()}RowMapper);
	}
#end
#end
#end