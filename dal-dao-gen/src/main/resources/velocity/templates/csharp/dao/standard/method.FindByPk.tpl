#if($host.isTable())      
#if($host.generateAPI(42,49))
#if($host.getPrimaryKeys().size() == 1)
        /// <summary>
        /// 根据主键获取${host.getClassName()}信息
        /// </summary>
#set($pk = $host.getPrimaryKeys().get(0))
        /// <param name="${WordUtils.uncapitalize($pk.getName())}"></param>
        /// <returns>${host.getClassName()}信息</returns>
        public ${host.getClassName()} FindByPk(${pk.getType()} ${WordUtils.uncapitalize($pk.getName())} )
        {
            try
            {
                return baseDao.GetByKey<${host.getClassName()}>(${WordUtils.uncapitalize($pk.getName())});
            }
            catch (Exception ex)
            {
                throw new DalException("调用${host.getClassName()}Dao时，访问FindByPk时出错", ex);
            }
        }
#elseif($host.getPrimaryKeys().size() >= 2)
        /// <summary>
        /// 根据主键获取${host.getClassName()}信息
        /// </summary>
#foreach ($cpk in $host.getPrimaryKeys())
        /// <param name="${WordUtils.uncapitalize($cpk.getName())}"></param>
#end
        /// <returns>${host.getClassName()}信息</returns>
        public ${host.getClassName()} FindByPk(#foreach ($cpk in $host.getPrimaryKeys())${cpk.getType()} ${WordUtils.uncapitalize($cpk.getName())}#if($foreach.count != $host.getPrimaryKeys().size()),#end#end)
        {
            try
            {
#set($array = [])
#foreach ($cpk in $host.getPrimaryKeys())
#set($current = $cpk.getName() + " = @" + $cpk.getName())
#set($success = $array.add($current))
#end
                string sql = @"select ${StringUtils.join($host.getColumns(), ", ")} from ${host.getTableName()} (nolock) where ${StringUtils.join($array, " and ")}";
                StatementParameterCollection parameters = new StatementParameterCollection();
#foreach ($cpk in $host.getPrimaryKeys())
                parameters.Add(new StatementParameter{Name = "${cpk.getName()}", DbType = DbType.${cpk.getDbType()}, Value = ${WordUtils.uncapitalize($cpk.getName())}});
#end

                return baseDao.SelectFirst<${host.getClassName()}>(sql, parameters);
            }
            catch (Exception ex)
            {
                throw new DalException("调用${host.getClassName()}Dao时，访问FindByPk时出错", ex);
            }
        }
#end
#end
#end
