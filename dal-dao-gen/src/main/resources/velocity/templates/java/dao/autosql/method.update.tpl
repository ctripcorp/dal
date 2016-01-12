#foreach($method in $host.getMethods())
#if($method.getCrud_type() == "update")
	/**
	 * ${method.getComments()}
	**/
	public int ${method.getName()} (${method.getUpdateParameterDeclaration()}) throws SQLException {
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
		UpdateSqlBuilder builder = new UpdateSqlBuilder("${method.getTableName()}", dbCategory);
#foreach($p in $method.getUpdateSetParameters())
		builder.update("${p.getName()}", ${p.getAlias()}, ${p.getJavaTypeDisplay()});
#end	
#parse("templates/java/dao/autosql/common.statement.parameters.tpl")
		String sql = builder.build();
		return client.update(sql, builder.buildParameters(), hints);
	}
#end
#end