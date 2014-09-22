#foreach($method in $host.getMethods())
#if(!$method.isFirstOrSingle() && $method.getCrud_type()!="update")
		/// <summary>
        ///  ${method.getName()}
        /// </summary>
#foreach($p in $method.getParameters())
        /// <param name="${WordUtils.uncapitalize($p.getName())}"></param>
#end
        /// <returns></returns>
        public IList<${method.getPojoName()}> ${method.getName()}(#foreach($p in $method.getParameters())#if($p.isInParameter())List<${p.getType()}>#{else}${p.getType()}#end ${WordUtils.uncapitalize($p.getName())}#if($foreach.count != $method.getParameters().size()),#end#end#if($method.isPaging())#if($method.getParameters().size()!=0),#end int pageNo, int pageSize#end)
        {
        	try
            {
				String sql = "${method.getSql()}";
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
#if($method.isPaging())
		        sql = string.Format(sql, ${host.pageBegain()}, ${host.pageEnd()},
					#foreach($p in $inParams)Arch.Data.Utility.ParameterUtility.NormalizeInParam(${WordUtils.uncapitalize($p.getAlias())}, parameters,"${WordUtils.uncapitalize($p.getAlias())}")#if($foreach.count != $inParams.size()),#end#end);
#else
		        sql = string.Format(sql, #foreach($p in $inParams)Arch.Data.Utility.ParameterUtility.NormalizeInParam(${WordUtils.uncapitalize($p.getAlias())}, parameters,"${WordUtils.uncapitalize($p.getAlias())}")#if($foreach.count != $inParams.size()),#end#end);
#end
#elseif($method.isPaging())
		        sql = string.Format(sql, ${host.pageBegain()}, ${host.pageEnd()});
#end
				//如果只需要一条记录，建议使用limit 1或者top 1，并使用SelectFirst提高性能
				//return baseDao.SelectFirst<${method.getPojoName()}>(sql, parameters);
                return baseDao.SelectList<${method.getPojoName()}>(sql, parameters);

            }
            catch (Exception ex)
            {
                throw new DalException("调用${host.getClassName()}Dao时，访问${method.getName()}时出错", ex);
            }
        }
#end
#end