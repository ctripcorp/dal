#if($host.isTable())      
#if($host.getPrimaryKeys().size() == 0)
        /*由于没有PK，不能生成Update方法
#end
#if($host.generateAPI(47,55))
        /// <summary>
        /// 修改${host.getClassName()}
        /// </summary>
        /// <param name="${WordUtils.uncapitalize($host.getClassName())}">${host.getClassName()}实体对象</param>
        /// <returns>状态代码</returns>
        public int Update${host.getClassName()}(${host.getClassName()} ${WordUtils.uncapitalize($host.getClassName())})
        {
            try
            {
                Object result = baseDao.Update<${host.getClassName()}>(${WordUtils.uncapitalize($host.getClassName())});
                int iReturn = Convert.ToInt32(result);

                return iReturn;
            }
            catch (Exception ex)
            {
                throw new DalException("调用${host.getClassName()}时，访问Update时出错", ex);
            }
        }
#end
#if($host.getPrimaryKeys().size() == 0)
        */
#end
#end
