#if($host.generateAPI(54))
#if($host.isHasSptI())
        /// <summary>
        ///  批量插入${host.getClassName()}
        /// </summary>
        /// <param name="${WordUtils.uncapitalize($host.getClassName())}">${host.getClassName()}实体对象列表</param>
        /// <returns>状态代码</returns>
        public int BulkInsert${host.getClassName()}(IList<${host.getClassName()}> ${WordUtils.uncapitalize($host.getClassName())}List)
        {
            try
            {
                DataTable dt = this.ToDataTable(${WordUtils.uncapitalize($host.getClassName())}List,true);

                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter { Value = dt, Name = "TVP_${host.getTableName()}", ExtendTypeValue = SqlDbType.Structured, ExtendType = 1 });
                parameters.Add(new StatementParameter { Name = "@return", Direction = ParameterDirection.ReturnValue });

                baseDao.ExecSp("spT_${host.getTableName()}_i", parameters);
                return (int)parameters["@return"].Value;
               
            }
            catch (Exception ex)
            {
                throw new DalException("调用${host.getClassName()}Dao时，访问BulkInsert时出错", ex);
            }
        }
#end
#end
#if($host.generateAPI(74))
#if($host.getDatabaseCategory().name() == "MySql" )
		/// <summary>
        ///  批量插入${host.getClassName()}
        /// </summary>
        /// <param name="${WordUtils.uncapitalize(${host.getClassName()})}">${host.getClassName()}实体对象列表</param>
        /// <returns>状态代码</returns>
        public bool BulkInsert${host.getClassName()}(IList<${host.getClassName()}> ${WordUtils.uncapitalize(${host.getClassName()})}List)
		{
            try
            {
                return baseDao.BulkInsert<${host.getClassName()}>(${WordUtils.uncapitalize(${host.getClassName()})}List);
            }
            catch (Exception ex)
            {
                throw new DalException("调用${host.getClassName()}Dao时，访问BulkInsert时出错", ex);
            }
        }	
#end
#end

