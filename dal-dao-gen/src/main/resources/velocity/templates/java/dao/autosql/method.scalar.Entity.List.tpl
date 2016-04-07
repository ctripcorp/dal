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
#if($method.isAllShard())
		hints.inAllShards();
#end
#if($method.isShards())
		hints.inShards(shards);
#end
#if($method.isAsync())
		hints.asyncExecution();
#end
#if($method.isCallback())
		hints.callbackWith(callback);
#end
		SelectSqlBuilder builder = new SelectSqlBuilder("${method.getTableName()}", dbCategory, $isPagination);
		builder.select(${method.getField()});
#parse("templates/java/dao/autosql/common.statement.parameters.tpl")
#if($method.getOrderByExp()!="")
		builder.orderBy(${method.getOrderByExp()});
#end
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