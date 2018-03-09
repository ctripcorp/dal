#if($host.generateAPI(45,52))
#if($host.isHasPagination())
        /// <summary>
        ///  检索${host.getClassName()}，带翻页
        /// </summary>
        /// <param name="obj">${host.getClassName()}实体对象检索条件</param>
        /// <param name="pagesize">每页记录数</param>
        /// <param name="pageNo">页码</param>
        /// <returns>检索结果</returns>
        public IList<${host.getClassName()}> GetListByPage(${host.getClassName()} obj, int pagesize, int pageNo)
        {
            try
            {
                StringBuilder sbSql = new StringBuilder(200);

#set($array = [])
#foreach ($cpk in $host.getPrimaryKeys())
#set($current = $cpk.getName() + " desc")
#set($success = $array.add($current))
#end
#if($host.getDatabaseCategory().name() == "MySql" )
                sbSql.Append(@"select ${StringUtils.join($host.getColumns(), ", ")} from ${host.getTableName()} ");
#if($host.getPrimaryKeys().size() > 0)
                sbSql.Append(" order by ${StringUtils.join($array, ", ")} ");
#else
                sbSql.Append(" order by ${host.getColumns().get(0)}  desc");
#end
                sbSql.Append(string.Format("limit {0}, {1} ", (pageNo - 1) * pagesize, pagesize));
#else
                sbSql.Append(@"select ${StringUtils.join($host.getColumns(), ", ")} from ${host.getTableName()} (nolock) ");
#if($host.getPrimaryKeys().size() > 0)
                sbSql.Append(" order by ${StringUtils.join($array, ", ")} ");
#else
                sbSql.Append(" order by ${host.getColumns().get(0)}  desc");
#end
                sbSql.Append(string.Format("OFFSET {0} ROWS FETCH NEXT {1} ROWS ONLY", (pageNo - 1) * pagesize, pagesize));
#end
                IList<${host.getClassName()}> list = baseDao.SelectList<${host.getClassName()}>(sbSql.ToString());
                return list;
            }
            catch (Exception ex)
            {
                throw new DalException("调用${host.getClassName()}Dao时，访问GetListByPage时出错", ex);
            }
        }
#end
#end