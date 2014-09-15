#foreach($method in $host.getExtraMethods())
        /// <summary>
        ///  ${method.getName()}
        /// </summary>
#foreach($p in $method.getParameters())
        /// <param name="${WordUtils.uncapitalize($p.getName())}"></param>
#end
        /// <returns></returns>
#if($method.isFirstOrSingle())
	#if($method.isSampleType())
		public object ${method.getName()} (#foreach($p in $method.getParameters())#if($p.isInParameter())List<${p.getType()}>#{else}${p.getType()}#end ${WordUtils.uncapitalize($p.getAlias())}#if($foreach.count != $method.getParameters().size()),#end#end#if($method.isPaging())#if($method.getParameters().size()!=0),#end int pageNo, int pageSize#end)
	#else
		public ${host.getClassName()} ${method.getName()} (#foreach($p in $method.getParameters())#if($p.isInParameter())List<${p.getType()}>#{else}${p.getType()}#end ${WordUtils.uncapitalize($p.getAlias())}#if($foreach.count != $method.getParameters().size()),#end#end#if($method.isPaging())#if($method.getParameters().size()!=0),#end int pageNo, int pageSize#end)
	#end
#else
		public #if($method.getCrud_type() == "select")IList<${host.getClassName()}>#{else}int#end ${method.getName()}(#foreach($p in $method.getParameters())#if($p.isInParameter())List<${p.getType()}>#{else}${p.getType()}#end ${WordUtils.uncapitalize($p.getAlias())}#if($foreach.count != $method.getParameters().size()),#end#end#if($method.isPaging())#if($method.getParameters().size()!=0),#end int pageNo, int pageSize#end)
#end
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();
				String sql = "${method.getSql()}";
#set($inParams = [])                
#foreach($p in $method.getParameters())  
#if($p.isInParameter())
#set($success = $inParams.add($p))
#else
                parameters.Add(new StatementParameter{ Name = "@${p.getAlias()}", Direction = ParameterDirection.Input, DbType = DbType.${p.getDbType()}, Value =${WordUtils.uncapitalize($p.getAlias())} });
#end
#end
#if($inParams.size() > 0)
#if($method.isPaging())
                sql = string.Format(sql, ${host.pageBegain()}, ${host.pageEnd()}, 
					#foreach($p in $inParams)Arch.Data.Utility.ParameterUtility.NormalizeInParam(${WordUtils.uncapitalize($p.getAlias())}, parameters,"${WordUtils.uncapitalize($p.getAlias())}")#if($foreach.count != $inParams.size()),#end#end);
#else
		        sql = string.Format(sql, #foreach($p in $inParams)Arch.Data.Utility.ParameterUtility.NormalizeInParam(${WordUtils.uncapitalize($p.getAlias())}, parameters,"${WordUtils.uncapitalize($p.getAlias())}")#if($foreach.count != $inParams.size()),#end#end);
#end
#elseif($method.isPaging())
		        sql = string.Format(sql, ${host.pageBegain()}, ${host.pageEnd()});
#end

#if($method.isFirstOrSingle())
	#if($method.isSampleType())
	            return baseDao.ExecScalar(sql, parameters);
	#else
			    return baseDao.SelectFirst<${host.getClassName()}>(sql, parameters);
	#end
#elseif($method.getCrud_type() == "select")
		        return baseDao.SelectList<${host.getClassName()}>(sql, parameters);
#else
		        return baseDao.ExecNonQuery(sql, parameters);
#end

            }
            catch (Exception ex)
            {
                throw new DalException("调用${host.getClassName()}Dao时，访问${method.getName()}时出错", ex);
            }
        }
#end