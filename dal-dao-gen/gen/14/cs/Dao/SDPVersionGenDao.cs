using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Text;
using Arch.Data;
using Arch.Data.DbEngine;
using DAL.Entity.DataModel;
using DAL.Interface.IDao;

namespace DAL.Dao
{
   /// <summary>
    /// 更多DAL接口功能，请参阅DAL Confluence，地址：
    /// http://conf.ctripcorp.com/display/SysDev/Dal+Fx+API
    /// </summary>
    public partial class SDPVersionGenDao : ISDPVersionGenDao
    {
        readonly BaseDao baseDao = BaseDaoFactory.CreateBaseDao("AssembleDB");
        
        /// <summary>
        ///  插入SDPVersionGen
        /// </summary>
        /// <param name="sDPVersionGen">SDPVersionGen实体对象</param>
        /// <returns>状态代码</returns>
        public int InsertSDPVersionGen(SDPVersionGen sDPVersionGen)
        {
            //没有Insert相关SP,请检查数据库后重新生成。
            return 0;
        }
        
        /// <summary>
        /// 修改SDPVersionGen
        /// </summary>
        /// <param name="sDPVersionGen">SDPVersionGen实体对象</param>
        /// <returns>状态代码</returns>
        public int UpdateSDPVersionGen(SDPVersionGen sDPVersionGen)
        {
            //没有Update相关SP,请检查数据库后重新生成。
            return 0;
        }
        
        /// <summary>
        /// 删除SDPVersionGen
        /// </summary>
        /// <param name="sDPVersionGen">SDPVersionGen实体对象</param>
        /// <returns>状态代码</returns>
        public int DeleteSDPVersionGen(SDPVersionGen sDPVersionGen)
        {
            //没有Delete相关SP,请检查数据库后重新生成。
            return 0;
        }
        
        

        /// <summary>
        /// 根据主键获取SDPVersionGen信息
        /// </summary>
        /// <param name="iD"></param>
        /// <returns>SDPVersionGen信息</returns>
        public SDPVersionGen FindByPk(long iD )
        {
            try
            {
                return baseDao.GetByKey<SDPVersionGen>(iD);
            }
            catch (Exception ex)
            {
                throw new DalException("调用SDPVersionGenDao时，访问FindByPk时出错", ex);
            }
        }

        /// <summary>
        /// 获取所有SDPVersionGen信息
        /// </summary>
        /// <returns>SDPVersionGen列表</returns>
        public IList<SDPVersionGen> GetAll()
        {
            try
            {
                return baseDao.GetAll<SDPVersionGen>();
            }
            catch (Exception ex)
            {
                throw new DalException("调用SDPVersionGenDao时，访问GetAll时出错", ex);
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
                String sql = "SELECT count(1) from SDP_Version  with (nolock)  ";

                object obj = baseDao.ExecScalar(sql);
                long ret = Convert.ToInt64(obj);
                return ret;
            }
            catch (Exception ex)
            {
                throw new DalException("调用SDPVersionGenDao时，访问Count时出错", ex);
            }
        }
        
        /// <summary>
        ///  检索SDPVersionGen，带翻页
        /// </summary>
        /// <param name="obj">SDPVersionGen实体对象检索条件</param>
        /// <param name="pagesize">每页记录数</param>
        /// <param name="pageNo">页码</param>
        /// <returns>检索结果</returns>
        public IList<SDPVersionGen> GetListByPage(SDPVersionGen obj, int pagesize, int pageNo)
        {
             try
            {
                var dic = new StatementParameterCollection();
                StringBuilder sbSql = new StringBuilder(200);

                
                 //计算ROWNUM
                int fromRownum = (pageNo - 1) * pagesize + 1;
                int endRownum = pagesize * pageNo;
                 sbSql.Append("WITH CTE AS ("); //WITH CTE 开始
                sbSql.Append("select row_number() over(order by ID desc ) as rownum, ");
                sbSql.Append(@"ID, TableName, Version, Flag from SDP_Version (nolock) ");

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
                sbSql.Append(@"select ID, TableName, Version, Flag from CTE Where rownum between @from and @end");
                dic.AddInParameter("@from", DbType.Int32, fromRownum);
                dic.AddInParameter("@end", DbType.Int32, endRownum);
                IList<SDPVersionGen> list = baseDao.SelectList<SDPVersionGen>(sbSql.ToString(), dic);

                return list;
            }
            catch (Exception ex)
            {
                throw new DalException("调用SDPVersionGenDao时，访问GetListByPage时出错", ex);
            }
        }






        
    }
}