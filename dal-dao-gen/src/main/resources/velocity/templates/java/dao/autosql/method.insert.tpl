#foreach($method in $host.getMethods())
#if($method.getCrud_type() == "insert")

    /**
	 * ${method.getComments()}
	**/
	public int ${method.getName()}(${method.getParameterDeclarationWithoutHints()}) throws SQLException {
		return ${method.getName()}(${method.getActualParameter()});
	}

	/**
	 * ${method.getComments()}
	**/
	public int ${method.getName()}(${method.getParameterDeclaration()}) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
		#parse("templates/java/Hints.java.tpl")

		InsertSqlBuilder builder = new InsertSqlBuilder();
#foreach($p in $method.getParameters())
#set($sensitiveflag = "")	
#if(${p.isSensitive()})
#set($sensitiveflag = "Sensitive")
#end
		builder.set$!{sensitiveflag}("${p.getName()}", ${p.getAlias()}, ${p.getJavaTypeDisplay()});
#end

		return client.insert(builder, hints);
	}
#end
#end