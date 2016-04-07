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
	public List<${host.getPojoClassName()}> ${method.getName()}(${method.getParameterDeclaration()}) throws SQLException {
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
	    String sql = builder.build();
		StatementParameters parameters = builder.buildParameters();
#if($method.isPaging())
		int index =  builder.getStatementParameterIndex();
		parameters.set(index++, Types.INTEGER, ${host.pageBegain()});
		parameters.set(index++, Types.INTEGER, ${host.pageEnd()});
#end
		return client.query(builder, hints);
	}
#end
#end
#end