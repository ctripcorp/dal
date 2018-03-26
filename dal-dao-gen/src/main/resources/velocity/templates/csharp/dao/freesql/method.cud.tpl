#foreach($method in $host.getMethods())
#if($method.getCrud_type()=="update")
	    /// <summary> 
        /// ${method.getName()}
        /// </summary> 
#foreach($p in $method.getParameters())
        /// <param name="${WordUtils.uncapitalize($p.getName())}"></param>
#end
        /// <returns>影响的行数</returns> 
		public int ${method.getName()} (${method.getParameterDeclaration()})
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
                parameters.Add(new StatementParameter{ Name = "@${p.getSqlParamName()}", Direction = ParameterDirection.Input, DbType = DbType.${p.getDbType()}, Value =${WordUtils.uncapitalize($p.getName())} });
#end
#end
#if($inParams.size() > 0)
                sql = string.Format(sql, #foreach($p in $inParams)Arch.Data.Utility.ParameterUtility.NormalizeInParam(${WordUtils.uncapitalize($p.getAlias())}, parameters,"${WordUtils.uncapitalize($p.getAlias())}")#if($foreach.count != $inParams.size()),#end#end);
#end
                return baseDao.ExecNonQuery(sql, parameters);
			}catch (Exception ex)
            {
                throw new DalException("调用${host.getClassName()}Dao时，访问${method.getName()}时出错", ex);
            }
		}
#end
#end