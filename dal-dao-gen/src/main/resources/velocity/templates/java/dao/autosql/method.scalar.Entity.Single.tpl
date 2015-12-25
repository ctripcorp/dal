#foreach($method in $host.getMethods())
#if($method.getCrud_type() == "select")
##实体类型且返回Single
#if($method.isReturnSingle() && !$method.isSampleType())
	/**
	 * ${method.getComments()}
	**/
	public ${host.getPojoClassName()} ${method.getName()}(${method.getParameterDeclaration()}) throws SQLException {
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
		SelectSqlBuilder builder = new SelectSqlBuilder("${method.getTableName()}", dbCategory, false);
		builder.select(${method.getField()});
#parse("templates/java/dao/autosql/common.statement.parameters.tpl")
	    String sql = builder.build();
		return queryDao.queryForObjectNullable(sql, builder.buildParameters(), hints, parser);
	}
#end
#end
#end