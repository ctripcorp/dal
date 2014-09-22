#if($host.isTable())      
#if($host.generateAPI(46,53))
        /// <summary>
        ///  插入${host.getClassName()}
        /// </summary>
        /// <param name="${WordUtils.uncapitalize(${host.getClassName()})}">${host.getClassName()}实体对象</param>
#if($host.isSpa())
        /// <returns>状态代码</returns>
#else
        /// <returns>新增的主键</returns>
#end
        public int Insert${host.getClassName()}(${host.getClassName()} ${WordUtils.uncapitalize($host.getClassName())})
        {
#if($host.isSpa())
#if($host.getSpaInsert().isExist())
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();
#foreach ($p in $host.getSpaInsert().getParameters())
                parameters.Add(new StatementParameter{ Name = "${p.getName()}", Direction = ParameterDirection.${p.getDirection()}, DbType = DbType.${p.getDbType()}, Value = ${WordUtils.uncapitalize($host.getClassName())}.${WordUtils.capitalize($p.getName().replace("@",""))}});
#end
                parameters.Add(new StatementParameter{ Name = "@return",  Direction = ParameterDirection.ReturnValue});

                baseDao.ExecSp("${host.getSpaInsert().getMethodName()}", parameters);

#foreach ($p in $host.getSpaInsert().getParameters())
#if($p.getDirection().name() == "Output" || $p.getDirection().name() == "InputOutput")
               ${WordUtils.uncapitalize($host.getClassName())}.${WordUtils.capitalize($p.getName().replace("@",""))} = (${p.getType()})parameters["${p.getName()}"].Value;
#end
#end
                return (int)parameters["@return"].Value;
            }
            catch (Exception ex)
            {
                throw new DalException("调用${host.getClassName()}Dao时，访问Insert时出错", ex);
            }
#else
            //没有Insert相关SP,请检查数据库后重新生成。
            return 0;
#end            
#else
            try
            {
                Object result = baseDao.Insert<${host.getClassName()}>(${WordUtils.uncapitalize($host.getClassName())});
                int iReturn = Convert.ToInt32(result);

                return iReturn;
            }
            catch (Exception ex)
            {
                throw new DalException("调用${host.getClassName()}时，访问Insert时出错", ex);
            }
#end
        }
#end
#end
