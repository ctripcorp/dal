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
#if($host.isSpa())
#if($host.getSpaDelete().isExist())
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();
#foreach ($p in $host.getSpaDelete().getParameters())
                parameters.Add(new StatementParameter{ Name = "${p.getName()}", Direction = ParameterDirection.${p.getDirection()}, DbType = DbType.${p.getDbType()}, Value = ${WordUtils.uncapitalize($host.getClassName())}.${WordUtils.capitalize($p.getName().replace("@",""))}});
#end
                parameters.Add(new StatementParameter{ Name = "@return",  Direction = ParameterDirection.ReturnValue});

                baseDao.ExecSp("${host.getSpaDelete().getMethodName()}", parameters);

                return (int)parameters["@return"].Value;
            }
            catch (Exception ex)
            {
                throw new DalException("调用${host.getClassName()}Dao时，访问Delete时出错", ex);
            }
#else
            //没有Delete相关SP,请检查数据库后重新生成。
            return 0;
#end            
#else
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
#end
        }
#end
#if($host.isSpa())
#if($host.getSpaDelete().isExist())
#if($host.generateAPI(48,57,58))
        /// <summary>
        /// 删除${host.getClassName()}
        /// </summary>
#foreach ($p in $host.getSpaDelete().getParameters())
        /// <param name="${WordUtils.uncapitalize($p.getName().replace('@', ''))}">${WordUtils.uncapitalize($p.getName())} #></param>
#end
        /// <returns>状态代码</returns>
        public int Delete${host.getClassName()}(#foreach ($p in $host.getSpaDelete().getParameters())${p.getType()} ${WordUtils.uncapitalize($p.getName().replace("@",""))}#if($foreach.count != $host.getSpaDelete().getParameters().size()),#end#end)
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();
#foreach ($p in $host.getSpaDelete().getParameters())
                parameters.Add(new StatementParameter{ Name = "${p.getName()}", Direction = ParameterDirection.${p.getDirection()}, DbType = DbType.${p.getDbType()}, Value = ${WordUtils.uncapitalize($p.getName().replace("@",""))}});
#end
                parameters.Add(new StatementParameter{ Name = "@return",  Direction = ParameterDirection.ReturnValue});

                baseDao.ExecSp("${host.getSpaDelete().getMethodName()}", parameters);

                return (int)parameters["@return"].Value;
            }
            catch (Exception ex)
            {
                throw new DalException("调用${host.getClassName()}Dao时，访问Delete${host.getClassName()}时出错", ex);
            }
        }
#end
#end
#end
#if($host.getPrimaryKeys().size() == 0)
        */
#end
#end
