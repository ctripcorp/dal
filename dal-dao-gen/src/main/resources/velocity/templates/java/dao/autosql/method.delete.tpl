#foreach($method in $host.getMethods())
#if($method.getCrud_type() == "delete" )
	/**
	 * ${method.getComments()}
	**/
	public int ${method.getName()} (${method.getParameterDeclaration()}) throws SQLException {
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
		DeleteSqlBuilder builder = new DeleteSqlBuilder("${method.getTableName()}", dbCategory);
#parse("templates/java/dao/autosql/common.statement.parameters.tpl")
		return client.delete(builder, hints);
	}
#end
#end