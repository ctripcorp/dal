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
                var dic = new StatementParameterCollection();
                StringBuilder sbSql = new StringBuilder(200);

#set($array = [])
#foreach ($cpk in $host.getPrimaryKeys())
#set($current = $cpk.getName() + " desc")
#set($success = $array.add($current))
#end
                
#if($host.getDatabaseCategory().name() == "MySql" )
                sbSql.Append(@"select ${StringUtils.join($host.getColumns(), ", ")} from ${host.getTableName()} ");
                //包含查询条件
                //StringBuilder whereCondition = new StringBuilder();
                //if (!string.IsNullOrEmpty(obj.Name))
                //{
                //    //人名
                //    whereCondition.Append("Where Name like @Name ");
                //    dic.AddInParameter("@Name", DbType.String, "%" + obj.Name + "%");
                //}
                //sbSql.Append(whereCondition);
#if($host.getPrimaryKeys().size() > 0)
                sbSql.Append(" order by ${StringUtils.join($array, ", ")} ");
#else
                sbSql.Append(" order by ${host.getColumns().get(0)}  desc");
#end
                sbSql.Append( string.Format("limit {0}, {1} ", (pageNo - 1) * pagesize, pagesize));
#else
                 //计算ROWNUM
                int fromRownum = (pageNo - 1) * pagesize + 1;
                int endRownum = pagesize * pageNo;
                 sbSql.Append("WITH CTE AS ("); //WITH CTE 开始
#if($host.getPrimaryKeys().size() > 0)
                sbSql.Append("select row_number() over(order by ${StringUtils.join($array, ", ")} ) as rownum, ");
#else
                sbSql.Append("select row_number() over(order by ${host.getColumns().get(0)} desc ) as rownum, ");
#end
                sbSql.Append(@"${StringUtils.join($host.getColumns(),", ")} from ${host.getTableName()} (nolock) ");

                //包含查询条件
                //StringBuilder whereCondition = new StringBuilder();
                //if (!string.IsNullOrEmpty(obj.Name))
                //{
                //    //人名
                //    whereCondition.Append("Where Name like @Name ");
                //    dic.AddInParameter("@Name", DbType.String, "%" + obj.Name + "%");
                //}
                //sbSql.Append(whereCondition);

                sbSql.Append(")"); //WITH CTE 结束

                // 用 CTE 完成分页
                sbSql.Append(@"select ${StringUtils.join($host.getColumns(),", ")} from CTE Where rownum between @from and @end");
                dic.AddInParameter("@from", DbType.Int32, fromRownum);
                dic.AddInParameter("@end", DbType.Int32, endRownum);
#end
                IList<${host.getClassName()}> list = baseDao.SelectList<${host.getClassName()}>(sbSql.ToString(), dic);

                return list;
            }
            catch (Exception ex)
            {
                throw new DalException("调用${host.getClassName()}Dao时，访问GetListByPage时出错", ex);
            }
        }
#end
#end