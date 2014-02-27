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
    public partial class PrizeInfo_genDao : IPrizeInfo_genDao
    {
        readonly BaseDao baseDao = BaseDaoFactory.CreateBaseDao("HotelPubDB");
        
        /// <summary>
        ///  插入PrizeInfo_gen
        /// </summary>
        /// <param name="prizeInfo_gen">PrizeInfo_gen实体对象</param>
        /// <returns>状态代码</returns>
        public int InsertPrizeInfo_gen(PrizeInfo_gen prizeInfo_gen)
        {
            //没有Insert相关SP,请检查数据库后重新生成。
            return 0;
        }
        
        /// <summary>
        /// 修改PrizeInfo_gen
        /// </summary>
        /// <param name="prizeInfo_gen">PrizeInfo_gen实体对象</param>
        /// <returns>状态代码</returns>
        public int UpdatePrizeInfo_gen(PrizeInfo_gen prizeInfo_gen)
        {
            //没有Update相关SP,请检查数据库后重新生成。
            return 0;
        }
        
        /// <summary>
        /// 删除PrizeInfo_gen
        /// </summary>
        /// <param name="prizeInfo_gen">PrizeInfo_gen实体对象</param>
        /// <returns>状态代码</returns>
        public int DeletePrizeInfo_gen(PrizeInfo_gen prizeInfo_gen)
        {
            //没有Delete相关SP,请检查数据库后重新生成。
            return 0;
        }
        
        

        /// <summary>
        /// 根据主键获取PrizeInfo_gen信息
        /// </summary>
        /// <param name="prizeInfoID"></param>
        /// <returns>PrizeInfo_gen信息</returns>
        public PrizeInfo_gen FindByPk(int prizeInfoID )
        {
            try
            {
                return baseDao.GetByKey<PrizeInfo_gen>(prizeInfoID);
            }
            catch (Exception ex)
            {
                throw new DalException("调用PrizeInfo_genDao时，访问FindByPk时出错", ex);
            }
        }

        /// <summary>
        /// 获取所有PrizeInfo_gen信息
        /// </summary>
        /// <returns>PrizeInfo_gen列表</returns>
        public IList<PrizeInfo_gen> GetAll()
        {
            try
            {
                return baseDao.GetAll<PrizeInfo_gen>();
            }
            catch (Exception ex)
            {
                throw new DalException("调用PrizeInfo_genDao时，访问GetAll时出错", ex);
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
                String sql = "SELECT count(1) from PrizeInfo  with (nolock)  ";

                object obj = baseDao.ExecScalar(sql);
                long ret = Convert.ToInt64(obj);
                return ret;
            }
            catch (Exception ex)
            {
                throw new DalException("调用PrizeInfo_genDao时，访问Count时出错", ex);
            }
        }
        
        /// <summary>
        ///  检索PrizeInfo_gen，带翻页
        /// </summary>
        /// <param name="obj">PrizeInfo_gen实体对象检索条件</param>
        /// <param name="pagesize">每页记录数</param>
        /// <param name="pageNo">页码</param>
        /// <returns>检索结果</returns>
        public IList<PrizeInfo_gen> GetListByPage(PrizeInfo_gen obj, int pagesize, int pageNo)
        {
             try
            {
                var dic = new StatementParameterCollection();
                StringBuilder sbSql = new StringBuilder(200);

                
                 //计算ROWNUM
                int fromRownum = (pageNo - 1) * pagesize + 1;
                int endRownum = pagesize * pageNo;
                 sbSql.Append("WITH CTE AS ("); //WITH CTE 开始
                sbSql.Append("select row_number() over(order by PrizeInfoID desc ) as rownum, ");
                sbSql.Append(@"PrizeInfoID, PrizeActivityid, PrizeType, PrizeLevel, PrizeName, EffectStartTime, EffectEndTime, DataChange_LastTime, PrizeCode from PrizeInfo (nolock) ");

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
                sbSql.Append(@"select PrizeInfoID, PrizeActivityid, PrizeType, PrizeLevel, PrizeName, EffectStartTime, EffectEndTime, DataChange_LastTime, PrizeCode from CTE Where rownum between @from and @end");
                dic.AddInParameter("@from", DbType.Int32, fromRownum);
                dic.AddInParameter("@end", DbType.Int32, endRownum);
                IList<PrizeInfo_gen> list = baseDao.SelectList<PrizeInfo_gen>(sbSql.ToString(), dic);

                return list;
            }
            catch (Exception ex)
            {
                throw new DalException("调用PrizeInfo_genDao时，访问GetListByPage时出错", ex);
            }
        }






        
    }
}