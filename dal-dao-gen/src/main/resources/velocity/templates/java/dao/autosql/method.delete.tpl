#foreach($method in $host.getMethods())
#if($method.getCrud_type() == "delete" )

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

		DeleteSqlBuilder builder = new DeleteSqlBuilder();
#parse("templates/java/dao/autosql/common.statement.parameters.tpl")

		return client.delete(builder, hints);
	}
#end
#end