using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Text;
using Arch.Data;
using Arch.Data.DbEngine;
using com.ctrip.platform.tools.Entity.DataModel;
using com.ctrip.platform.tools.IDao;

namespace com.ctrip.platform.tools
{
   /// <summary>
    /// 更多DALFx接口功能，请参阅DALFx Confluence，地址：
    /// http://conf.ctripcorp.com/display/ARCH/Dal+Fx+API
    /// </summary>
    public partial class Sysdiagrams_genDao : ISysdiagrams_genDao
    {
        readonly BaseDao baseDao = BaseDaoFactory.CreateBaseDao("AssembleDB");
        
        /// <summary>
        ///  插入Sysdiagrams_gen
        /// </summary>
        /// <param name="sysdiagrams_gen">Sysdiagrams_gen实体对象</param>
        /// <returns>状态代码</returns>
        public int InsertSysdiagrams_gen(Sysdiagrams_gen sysdiagrams_gen)
        {
            //没有Insert相关SP,请检查数据库后重新生成。
            return 0;
        }
        
        /// <summary>
        /// 修改Sysdiagrams_gen
        /// </summary>
        /// <param name="sysdiagrams_gen">Sysdiagrams_gen实体对象</param>
        /// <returns>状态代码</returns>
        public int UpdateSysdiagrams_gen(Sysdiagrams_gen sysdiagrams_gen)
        {
            //没有Update相关SP,请检查数据库后重新生成。
            return 0;
        }
        
        /// <summary>
        /// 删除Sysdiagrams_gen
        /// </summary>
        /// <param name="sysdiagrams_gen">Sysdiagrams_gen实体对象</param>
        /// <returns>状态代码</returns>
        public int DeleteSysdiagrams_gen(Sysdiagrams_gen sysdiagrams_gen)
        {
            //没有Delete相关SP,请检查数据库后重新生成。
            return 0;
        }
        
        

        /// <summary>
        /// 根据主键获取Sysdiagrams_gen信息
        /// </summary>
        /// <param name="diagram_id"></param>
        /// <returns>Sysdiagrams_gen信息</returns>
        public Sysdiagrams_gen FindByPk(int diagram_id )
        {
            try
            {
                return baseDao.GetByKey<Sysdiagrams_gen>(diagram_id);
            }
            catch (Exception ex)
            {
                throw new DalException("调用Sysdiagrams_genDao时，访问FindByPk时出错", ex);
            }
        }

        /// <summary>
        /// 获取所有Sysdiagrams_gen信息
        /// </summary>
        /// <returns>Sysdiagrams_gen列表</returns>
        public IList<Sysdiagrams_gen> GetAll()
        {
            try
            {
                return baseDao.GetAll<Sysdiagrams_gen>();
            }
            catch (Exception ex)
            {
                throw new DalException("调用Sysdiagrams_genDao时，访问GetAll时出错", ex);
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
                String sql = "SELECT count(1) from sysdiagrams  with (nolock)  ";

                object obj = baseDao.ExecScalar(sql);
                long ret = Convert.ToInt64(obj);
                return ret;
            }
            catch (Exception ex)
            {
                throw new DalException("调用Sysdiagrams_genDao时，访问Count时出错", ex);
            }
        }
        
        /// <summary>
        ///  检索Sysdiagrams_gen，带翻页
        /// </summary>
        /// <param name="obj">Sysdiagrams_gen实体对象检索条件</param>
        /// <param name="pagesize">每页记录数</param>
        /// <param name="pageNo">页码</param>
        /// <returns>检索结果</returns>
        public IList<Sysdiagrams_gen> GetListByPage(Sysdiagrams_gen obj, int pagesize, int pageNo)
        {
             try
            {
                var dic = new StatementParameterCollection();
                StringBuilder sbSql = new StringBuilder(200);

                
                 //计算ROWNUM
                int fromRownum = (pageNo - 1) * pagesize + 1;
                int endRownum = pagesize * pageNo;
                 sbSql.Append("WITH CTE AS ("); //WITH CTE 开始
                sbSql.Append("select row_number() over(order by diagram_id desc ) as rownum, ");
                sbSql.Append(@"name, principal_id, diagram_id, version, definition from sysdiagrams (nolock) ");

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
                sbSql.Append(@"select name, principal_id, diagram_id, version, definition from CTE Where rownum between @from and @end");
                dic.AddInParameter("@from", DbType.Int32, fromRownum);
                dic.AddInParameter("@end", DbType.Int32, endRownum);
                IList<Sysdiagrams_gen> list = baseDao.SelectList<Sysdiagrams_gen>(sbSql.ToString(), dic);

                return list;
            }
            catch (Exception ex)
            {
                throw new DalException("调用Sysdiagrams_genDao时，访问GetListByPage时出错", ex);
            }
        }






        
    }
}