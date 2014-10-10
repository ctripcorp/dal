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
		SelectSqlBuilder builder = new SelectSqlBuilder("${method.getTableName()}", dbCategory, $isPagination);
		builder.select(${method.getField()});
#parse("templates/java/dao/autosql/common.statement.parameters.tpl")
#if($method.getOrderByExp()!="")
		builder.orderBy(${method.getOrderByExp()});
#end
        String sql = builder.build();
#if($method.isPaging())
		sql = String.format(sql, ${host.pageBegain()}, ${host.pageEnd()});
#end
		return queryDao.query(sql, builder.buildParameters, hints, ${method.getPojoClassName()}.class);
	}
#end
#end
#end