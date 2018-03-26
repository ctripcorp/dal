#foreach($method in $host.getMethods())
#if($method.getCrud_type() == "update")

    /**
	 * ${method.getComments()}
	**/
	public int ${method.getName()}(${method.getUpdateParameterDeclarationWithoutHints()}) throws SQLException {
		return ${method.getName()}(${method.getUpdateActualParameter()});
	}

	/**
	 * ${method.getComments()}
	**/
	public int ${method.getName()}(${method.getUpdateParameterDeclaration()}) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
#parse("templates/java/Hints.java.tpl")

		UpdateSqlBuilder builder = new UpdateSqlBuilder();
#foreach($p in $method.getUpdateSetParameters())
		builder.update("${p.getName()}", ${p.getAlias()}, ${p.getJavaTypeDisplay()});
#end	
#parse("templates/java/dao/autosql/common.statement.parameters.tpl")

		return client.update(builder, hints);
	}
#end
#end