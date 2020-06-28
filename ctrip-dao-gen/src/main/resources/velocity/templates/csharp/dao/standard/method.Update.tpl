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
#if($host.isSpa())
#if($host.getSpaUpdate().isExist())
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();
#foreach ($p in $host.getSpaUpdate().getParameters())
                parameters.Add(new StatementParameter{ Name = "${p.getName()}", Direction = ParameterDirection.${p.getDirection()}, DbType = DbType.${p.getDbType()}, Value = ${WordUtils.uncapitalize($host.getClassName())}.${WordUtils.capitalize($p.getName().replace("@",""))}});
#end
                parameters.Add(new StatementParameter{ Name = "@return",  Direction = ParameterDirection.ReturnValue});

                baseDao.ExecSp("${host.getSpaUpdate().getMethodName()}", parameters);

#foreach ($p in $host.getSpaUpdate().getParameters())
#if($p.getDirection().name() == "Output" || $p.getDirection().name() == "InputOutput")
               ${WordUtils.uncapitalize($host.getClassName())}.${WordUtils.capitalize($p.getName().replace("@",""))} = (${p.getType()})parameters["${p.getName()}"].Value;
#end
#end
                return (int)parameters["@return"].Value;
            }
            catch (Exception ex)
            {
                throw new DalException("调用${host.getClassName()}Dao时，访问Update时出错", ex);
            }
#else
            //没有Update相关SP,请检查数据库后重新生成。
            return 0;
#end            
#else
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
#end
        }
#end
#if($host.getPrimaryKeys().size() == 0)
        */
#end
#end
