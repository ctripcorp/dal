#if($host.generateAPI(56))
#if($host.isHasSptU())
        /// <summary>
        ///  批量修改${host.getClassName()}
        /// </summary>
        /// <param name="${WordUtils.uncapitalize($host.getClassName())}">${host.getClassName()}实体对象列表</param>
        /// <returns>状态代码</returns>
        public int BulkUpdate${host.getClassName()}(IList<${host.getClassName()}> ${WordUtils.uncapitalize($host.getClassName())}List)
        {
            try
            {
                DataTable dt = this.ToDataTable(${WordUtils.uncapitalize($host.getClassName())}List,false);

                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter { Value = dt, Name = "TVP_${host.getTableName()}", ExtendTypeValue = SqlDbType.Structured, ExtendType = 1 });
                parameters.Add(new StatementParameter { Name = "@return", Direction = ParameterDirection.ReturnValue });

                baseDao.ExecSp("spT_${host.getTableName()}_u", parameters);
                return (int)parameters["@return"].Value;
               
            }
            catch (Exception ex)
            {
                throw new DalException("调用${host.getClassName()}Dao时，访问BulkUpdate时出错", ex);
            }
        }
#end
#end
