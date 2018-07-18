##简单类型并且返回值是First
#foreach($method in $host.getExtraMethods())
#if($method.isFirstOrSingle() && $method.isSampleType())
		/// <summary>
        ///  ${method.getName()}
        /// </summary>
#foreach($p in $method.getParameters())
        /// <param name="${WordUtils.uncapitalize($p.getName())}"></param>
#end
        /// <returns></returns>
		public object ${method.getName()} (${method.getParameterDeclaration()})
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();
				String sql = "${method.getSql($host.getDatabaseCategory())}";
#set($inParams = [])                
#foreach($p in $method.getParameters())  
#if($p.isInParameter())
#set($success = $inParams.add($p))
#else
                parameters.Add(new StatementParameter{ Name = "@${p.getSqlParamName()}", Direction = ParameterDirection.Input, DbType = DbType.${p.getDbType()}, Value =${WordUtils.uncapitalize($p.getAlias())} });
#end
#end
#if($inParams.size() > 0)
		        sql = string.Format(sql, #foreach($p in $inParams)Arch.Data.Utility.ParameterUtility.NormalizeInParam(${WordUtils.uncapitalize($p.getAlias())}, parameters,"${WordUtils.uncapitalize($p.getAlias())}")#if($foreach.count != $inParams.size()),#end#end);
#end

	            return baseDao.ExecScalar(sql, parameters);

            }
            catch (Exception ex)
            {
                throw new DalException("调用${host.getClassName()}Dao时，访问${method.getName()}时出错", ex);
            }
        }
#end
#end