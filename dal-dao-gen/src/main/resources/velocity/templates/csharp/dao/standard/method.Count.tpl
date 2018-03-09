#if($host.generateAPI(44,51))
        /// <summary>
        /// 取得总记录数
        /// </summary>
        /// <returns>记录数</returns>
        public long Count()
        {
            try
            {
                String sql = "SELECT count(1) from ${host.getTableName()} #if($host.getDatabaseCategory().name() == "MySql" ) #{else} with (nolock)#end  ";
                object obj = baseDao.ExecScalar(sql);
                long ret = Convert.ToInt64(obj);
                return ret;
            }
            catch (Exception ex)
            {
                throw new DalException("调用${host.getClassName()}Dao时，访问Count时出错", ex);
            }
        }
#end