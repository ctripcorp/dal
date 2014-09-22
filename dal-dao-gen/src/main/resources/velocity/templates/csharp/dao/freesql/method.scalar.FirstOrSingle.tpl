#foreach($method in $host.getMethods())
#if($method.isFirstOrSingle() && $method.getCrud_type()!="update")
#if($method.isSampleType())
		public object ${method.getName()} (#foreach($p in $method.getParameters())#if($p.isInParameter())List<${p.getType()}>#{else}${p.getType()}#end ${WordUtils.uncapitalize($p.getName())}#if($foreach.count != $method.getParameters().size()),#end#end)
#else
		public ${method.getPojoName()} ${method.getName()} (#foreach($p in $method.getParameters())#if($p.isInParameter())List<${p.getType()}>#{else}${p.getType()}#end ${WordUtils.uncapitalize($p.getName())}#if($foreach.count != $method.getParameters().size()),#end#end)
#end
		{
			try
			{
				string sql = "${method.getSql()}";
                StatementParameterCollection parameters = new StatementParameterCollection();
				#set($inParams = [])                
#foreach($p in $method.getParameters())  
#if($p.isInParameter())
#set($success = $inParams.add($p))
#else
                parameters.Add(new StatementParameter{ Name = "@${p.getName()}", Direction = ParameterDirection.Input, DbType = DbType.${p.getDbType()}, Value =${WordUtils.uncapitalize($p.getName())} });
#end
#end
#if($inParams.size() > 0)
                sql = string.Format(sql, #foreach($p in $inParams)Arch.Data.Utility.ParameterUtility.NormalizeInParam(${WordUtils.uncapitalize($p.getAlias())}, parameters,"${WordUtils.uncapitalize($p.getAlias())}")#if($foreach.count != $inParams.size()),#end#end);
#end
#if($method.isSampleType())
	            return baseDao.ExecScalar(sql, parameters);
#else
		        return baseDao.SelectFirst<${method.getPojoName()}>(sql, parameters);
#end
			}catch (Exception ex)
            {
                throw new DalException("调用${host.getClassName()}Dao时，访问${method.getName()}时出错", ex);
            }
		}
#end
#end
