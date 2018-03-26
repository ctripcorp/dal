#if($host.isTable())      
#if($host.getPrimaryKeys().size() == 0)
        /*由于没有PK，不能生成Delete方法
#end
#if($host.generateAPI(48,57,58,59))        
        /// <summary>
        /// 删除${host.getClassName()}
        /// </summary>
        /// <param name="${WordUtils.uncapitalize($host.getClassName())}">${host.getClassName()}实体对象</param>
        /// <returns>状态代码</returns>
        public int Delete${host.getClassName()}(${host.getClassName()} ${WordUtils.uncapitalize($host.getClassName())})
        {
            try
            {
                Object result = baseDao.Delete<${host.getClassName()}>(${WordUtils.uncapitalize($host.getClassName())});
                int iReturn = Convert.ToInt32(result);

                return iReturn;
            }
            catch (Exception ex)
            {
                throw new DalException("调用${host.getClassName()}时，访问Delete时出错", ex);
            }
        }
#end
#if($host.getPrimaryKeys().size() == 0)
        */
#end
#end
