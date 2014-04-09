using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Text;
using Arch.Data;
using Arch.Data.DbEngine;
using ${host.getNameSpace()}.Entity.DataModel;
using ${host.getNameSpace()}.Interface.IDao;

namespace ${host.getNameSpace()}.Dao
{
   /// <summary>
    /// 更多DAL接口功能，请参阅DAL Confluence，地址：
    /// http://conf.ctripcorp.com/display/SysDev/Dal+Fx+API
    /// </summary>
    public partial class ${host.getClassName()}Dao : I${host.getClassName()}Dao
    {
        readonly BaseDao baseDao = BaseDaoFactory.CreateBaseDao("${host.getDbSetName()}");
        
#if($host.isTable())
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
        
#if($host.getPrimaryKeys().size() == 0)
        /*由于没有PK，不能生成Update和Delete方法
#end
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
        
#if($host.isSpa())
#if($host.getSpaDelete().isExist())
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
        
#if($host.getPrimaryKeys().size() == 0)
        */
#end

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

#if($host.isHasSpt())
        /// <summary>
        ///  转换List为DataTable
        /// </summary>
        /// <param name="${WordUtils.uncapitalize($host.getClassName())}list">${host.getClassName()}实体对象列表</param>
        /// <param name="isInsert">${host.getClassName()}实体对象列表</param>
        /// <returns>DataTable</returns>
        private DataTable ToDataTable(IList<${host.getClassName()}> ${WordUtils.uncapitalize($host.getClassName())}List , bool insert)
        {
            DataTable dt = new DataTable();
#foreach($column in $host.getColumns())
            dt.Columns.Add("${WordUtils.capitalize($column.getName())}", typeof(${column.getType()}));
#end

            int i = 0;
            foreach (${host.getClassName()} ${WordUtils.uncapitalize($host.getClassName())}Info in ${WordUtils.uncapitalize($host.getClassName())}List)
            {
                DataRow row = dt.NewRow();
#foreach($column in $host.getColumns())
#if($column.isIdentity())
                row["${WordUtils.capitalize($column.getName())}"] = insert ? ++i : ${WordUtils.uncapitalize($host.getClassName())}Info.${WordUtils.capitalize($column.getName())};
#else
                row["${WordUtils.capitalize($column.getName())}"] = ${WordUtils.uncapitalize($host.getClassName())}Info.${WordUtils.capitalize($column.getName())};
#end
#end
                dt.Rows.Add(row);
            }
            return dt;
        }
#end


#if($host.isHasSptI())
        /// <summary>
        ///  批量插入${host.getClassName()}
        /// </summary>
        /// <param name="${WordUtils.uncapitalize($host.getClassName())}">${host.getClassName()}实体对象列表</param>
        /// <returns>状态代码</returns>
        public int BulkInsert${host.getClassName()}(IList<${host.getClassName()}> ${WordUtils.uncapitalize($host.getClassName())}List)
        {
            try
            {
                DataTable dt = this.ToDataTable(${WordUtils.uncapitalize($host.getClassName())}List,true);

                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter { Value = dt, Name = "TVP_${host.getTableName()}", ExtendTypeValue = SqlDbType.Structured, ExtendType = 1 });
                parameters.Add(new StatementParameter { Name = "@return", Direction = ParameterDirection.ReturnValue });

                baseDao.ExecSp("spT_${host.getTableName()}_i", parameters);
                return (int)parameters["@return"].Value;
               
            }
            catch (Exception ex)
            {
                throw new DalException("调用${host.getClassName()}Dao时，访问BulkInsert时出错", ex);
            }
        }
#end

#if($host.isHasSptU())
        /// <summary>
        ///  批量修改${host.getClassName()}
        /// </summary>
        /// <param name="${WordUtils.uncapitalize($host.getClassName())}">${host.getClassName()}实体对象列表</param>
        /// <returns>状态代码</returns>
        public int BulkUpdate${host.getClassName()}(IList<${host.getClassName()}> ${WordUtils.uncapitalize($host.getClassName())}List)
        {
            try
            {
                DataTable dt = this.ToDataTable(${WordUtils.uncapitalize($host.getClassName())}List,false);

                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter { Value = dt, Name = "TVP_${host.getTableName()}", ExtendTypeValue = SqlDbType.Structured, ExtendType = 1 });
                parameters.Add(new StatementParameter { Name = "@return", Direction = ParameterDirection.ReturnValue });

                baseDao.ExecSp("spT_${host.getTableName()}_u", parameters);
                return (int)parameters["@return"].Value;
               
            }
            catch (Exception ex)
            {
                throw new DalException("调用${host.getClassName()}Dao时，访问BulkUpdate时出错", ex);
            }
        }
#end

#if($host.isHasSptD())
        /// <summary>
        ///  批量删除${host.getClassName()}
        /// </summary>
        /// <param name="${WordUtils.uncapitalize($host.getClassName())}">${host.getClassName()}实体对象列表</param>
        /// <returns>状态代码</returns>
        public int BulkDelete${host.getClassName()}(IList<${host.getClassName()}> ${WordUtils.uncapitalize($host.getClassName())}List)
        {
            try
            {
                DataTable dt = this.ToDataTable(${WordUtils.uncapitalize($host.getClassName())}List,false);

                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter { Value = dt, Name = "TVP_${host.getTableName()}", ExtendTypeValue = SqlDbType.Structured, ExtendType = 1 });
                parameters.Add(new StatementParameter { Name = "@return", Direction = ParameterDirection.ReturnValue });

                baseDao.ExecSp("spT_${host.getTableName()}_d", parameters);
                return (int)parameters["@return"].Value;
               
            }
            catch (Exception ex)
            {
                throw new DalException("调用${host.getClassName()}Dao时，访问BulkDelete时出错", ex);
            }
        }
#end

#foreach($method in $host.getExtraMethods())
		/// <summary>
        ///  ${method.getName()}
        /// </summary>
#foreach($p in $method.getParameters())
        /// <param name="${WordUtils.uncapitalize($p.getName())}"></param>
#end
        /// <returns></returns>
        public #if($method.getCrud_type() == "select")IList<${host.getClassName()}>#{else}int#end ${method.getName()}(#foreach($p in $method.getParameters())${p.getType()} ${WordUtils.uncapitalize($p.getAlias())}#if($foreach.count != $method.getParameters().size()),#end#end)
        {
        	try
            {
            	string sql = "${method.getSql()}";
                StatementParameterCollection parameters = new StatementParameterCollection();
#foreach($p in $method.getParameters())  
                parameters.Add(new StatementParameter{ Name = "@${p.getName()}", Direction = ParameterDirection.Input, DbType = DbType.${p.getDbType()}, Value =${WordUtils.uncapitalize($p.getAlias())} });
#end

#if($method.getCrud_type() == "select")
                return baseDao.SelectList<${host.getClassName()}>(sql, parameters);
#else
				return baseDao.ExecNonQuery(sql, parameters);
#end

            }
            catch (Exception ex)
            {
                throw new DalException("调用${host.getClassName()}Dao时，访问${method.getName()}时出错", ex);
            }
        }
#end
        
    }
}