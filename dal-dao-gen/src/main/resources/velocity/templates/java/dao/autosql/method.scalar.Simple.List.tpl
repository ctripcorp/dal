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
		SelectSqlBuilder builder = new SelectSqlBuilder("${method.getTableName()}", dbCategory, $isPagination);
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
		return queryDao.query(sql, parameters, hints, ${method.getPojoClassName()}.class);
	}
#end
#end
#end