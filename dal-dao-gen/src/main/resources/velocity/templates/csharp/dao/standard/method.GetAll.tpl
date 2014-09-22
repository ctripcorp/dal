#if($host.generateAPI(43,50))
        /// <summary>
        /// 获取所有${host.getClassName()}信息
        /// </summary>
        /// <returns>${host.getClassName()}列表</returns>
        public IList<${host.getClassName()}> GetAll()
        {
            try
            {
                return baseDao.GetAll<${host.getClassName()}>();
            }
            catch (Exception ex)
            {
                throw new DalException("调用${host.getClassName()}Dao时，访问GetAll时出错", ex);
            }
        }
#end