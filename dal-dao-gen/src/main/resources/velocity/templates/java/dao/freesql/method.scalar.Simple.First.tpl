#foreach($method in $host.getMethods())
#if($method.getCrud_type()=="select")
##简单类型且返回值为First
#if($method.isSampleType() && $method.isReturnFirst())

    /**
	 * ${method.getComments()}
	**/
	public ${method.getPojoClassName()} ${method.getName()}(${method.getParameterDeclarationWithoutHints()}) throws SQLException {
		return ${method.getName()}(${method.getActualParameter()});
	}

	/**
	 * ${method.getComments()}
	**/
	public ${method.getPojoClassName()} ${method.getName()}(${method.getParameterDeclaration()}) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
#parse("templates/java/Hints.java.tpl")

		FreeSelectSqlBuilder<${method.getPojoClassName()}> builder = new FreeSelectSqlBuilder<>(dbCategory);
		builder.setTemplate("${method.getSql()}");
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
		builder.simpleType().requireFirst().nullable();

		return queryDao.query(builder, parameters, hints);
	}
#end
#end
#end