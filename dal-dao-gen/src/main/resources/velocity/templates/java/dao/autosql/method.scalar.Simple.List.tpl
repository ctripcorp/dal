#foreach($method in $host.getMethods())
#if($method.getCrud_type() == "select")
##简单类型并且返回值是List
#if($method.isSampleType() && $method.isReturnList())
#set($isPagination = "false")		
#if($method.isPaging())
#set($isPagination = "true")	
#end

	/**
	 * ${method.getComments()}
	**/
	public List<${method.getPojoClassName()}> ${method.getName()}(${method.getParameterDeclaration()}) throws SQLException {
		hints = DalHints.createIfAbsent(hints);
#parse("templates/java/Hints.java.tpl")

#if($method.isPaging())
		SelectSqlBuilder builder = new SelectSqlBuilder("${method.getTableName()}", dbCategory, pageNo, pageSize);
#else
		SelectSqlBuilder builder = new SelectSqlBuilder("${method.getTableName()}", dbCategory);
#end
		builder.select(${method.getField()});
#parse("templates/java/dao/autosql/common.statement.parameters.tpl")
#if($method.getOrderByExp()!="")
		builder.orderBy(${method.getOrderByExp()});
#end

		return client.query(builder, hints, ${method.getPojoClassName()}.class);
	}
#end
#end
#end