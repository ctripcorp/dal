#foreach($method in $host.getMethods())
#if($method.getCrud_type() == "select")
##实体类型并且返回值为List
#if($method.isReturnList() && !$method.isSampleType())
#set($isPagination = "false")		
#if($method.isPaging())
#set($isPagination = "true")	
#end

    /**
	 * ${method.getComments()}
	**/
	public List<${host.getPojoClassName()}> ${method.getName()}(${method.getParameterDeclarationWithoutHints()}) throws SQLException {
		return ${method.getName()}(${method.getActualParameter()});
	}

	/**
	 * ${method.getComments()}
	**/
	public List<${host.getPojoClassName()}> ${method.getName()}(${method.getParameterDeclaration()}) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
#parse("templates/java/Hints.java.tpl")

		SelectSqlBuilder builder = new SelectSqlBuilder();
		builder.select(${method.getField()});
#parse("templates/java/dao/autosql/common.statement.parameters.tpl")
#if($method.getOrderByExp()!="")
		builder.orderBy(${method.getOrderByExp()});
#end
#if($method.isPaging())
		builder.atPage(pageNo, pageSize);
#end

		return client.query(builder, hints);
	}
#end
#end
#end