##实体类型或简单类型且返回List
#foreach($method in $host.getExtraMethods())
#if(!$method.isFirstOrSingle() && $method.getCrud_type() == "select" && $method.isPaging())
		/// <summary>
        ///  ${method.getName()}
        /// </summary>
#foreach($p in $method.getParameters())
        /// <param name="${WordUtils.uncapitalize($p.getName())}"></param>
#end
        /// <returns></returns>
		public IList<${host.getClassName()}> ${method.getName()}(${method.getParameterDeclaration()})
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

	            return baseDao.SelectList<${host.getClassName()}>(sql, parameters);

            }
            catch (Exception ex)
            {
                throw new DalException("调用${host.getClassName()}Dao时，访问${method.getName()}时出错", ex);
            }
        }
#end
#end