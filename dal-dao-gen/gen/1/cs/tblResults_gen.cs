using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Text;
using Arch.Data;
using Arch.Data.DbEngine;
using com.ctrip.platform.tools.Entity.DataModel;
using com.ctrip.platform.tools.Interface.IDao;

namespace com.ctrip.platform.tools.Dao
{
   /// <summary>
    /// 更多DALFx接口功能，请参阅DALFx Confluence，地址：
    /// http://conf.ctripcorp.com/display/ARCH/Dal+Fx+API
    /// </summary>
    public partial class tblResults_genDao : ItblResults_genDao
    {
        readonly BaseDao baseDao = BaseDaoFactory.CreateBaseDao("SysDalTest");
        
        /// <summary>
        ///  插入tblResults_gen
        /// </summary>
        /// <param name="tblResults_gen">tblResults_gen实体对象</param>
        /// <returns>状态代码</returns>
        public int InserttblResults_gen(tblResults_gen tblResults_gen)
        {
            //没有Insert相关SP,请检查数据库后重新生成。
            return 0;
        }
        
        /// <summary>
        /// 修改tblResults_gen
        /// </summary>
        /// <param name="tblResults_gen">tblResults_gen实体对象</param>
        /// <returns>状态代码</returns>
        public int UpdatetblResults_gen(tblResults_gen tblResults_gen)
        {
            //没有Update相关SP,请检查数据库后重新生成。
            return 0;
        }
        
        /// <summary>
        /// 删除tblResults_gen
        /// </summary>
        /// <param name="tblResults_gen">tblResults_gen实体对象</param>
        /// <returns>状态代码</returns>
        public int DeletetblResults_gen(tblResults_gen tblResults_gen)
        {
            //没有Delete相关SP,请检查数据库后重新生成。
            return 0;
        }
        
        


        /// <summary>
        /// 获取所有tblResults_gen信息
        /// </summary>
        /// <returns>tblResults_gen列表</returns>
        public IList<tblResults_gen> GetAll()
        {
            try
            {
                return baseDao.GetAll<tblResults_gen>();
            }
            catch (Exception ex)
            {
                throw new DalException("调用tblResults_genDao时，访问GetAll时出错", ex);
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
                String sql = "SELECT count(1) from tblResults  with (nolock)  ";

                object obj = baseDao.ExecScalar(sql);
                long ret = Convert.ToInt64(obj);
                return ret;
            }
            catch (Exception ex)
            {
                throw new DalException("调用tblResults_genDao时，访问Count时出错", ex);
            }
        }
        
        /// <summary>
        ///  检索tblResults_gen，带翻页
        /// </summary>
        /// <param name="obj">tblResults_gen实体对象检索条件</param>
        /// <param name="pagesize">每页记录数</param>
        /// <param name="pageNo">页码</param>
        /// <returns>检索结果</returns>
        public IList<tblResults_gen> GetListByPage(tblResults_gen obj, int pagesize, int pageNo)
        {
             try
            {
                var dic = new StatementParameterCollection();
                StringBuilder sbSql = new StringBuilder(200);

                
                 //计算ROWNUM
                int fromRownum = (pageNo - 1) * pagesize + 1;
                int endRownum = pagesize * pageNo;
                 sbSql.Append("WITH CTE AS ("); //WITH CTE 开始
                sbSql.Append("select row_number() over(order by name desc ) as rownum, ");
                sbSql.Append(@"name, status, indid, OrigFillFactor, IndCol1, IndCol2, IndCol3, IndCol4, IndCol5, IndCol6, IndCol7, IndCol8, IndCol9, IndCol10, IndCol11, IndCol12, IndCol13, IndCol14, IndCol15, IndCol16, SegName, FullTextKey, Descending, Computed, IsTable from tblResults (nolock) ");

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
                sbSql.Append(@"select name, status, indid, OrigFillFactor, IndCol1, IndCol2, IndCol3, IndCol4, IndCol5, IndCol6, IndCol7, IndCol8, IndCol9, IndCol10, IndCol11, IndCol12, IndCol13, IndCol14, IndCol15, IndCol16, SegName, FullTextKey, Descending, Computed, IsTable from CTE Where rownum between @from and @end");
                dic.AddInParameter("@from", DbType.Int32, fromRownum);
                dic.AddInParameter("@end", DbType.Int32, endRownum);
                IList<tblResults_gen> list = baseDao.SelectList<tblResults_gen>(sbSql.ToString(), dic);

                return list;
            }
            catch (Exception ex)
            {
                throw new DalException("调用tblResults_genDao时，访问GetListByPage时出错", ex);
            }
        }





        
    }
}
