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
    public partial class SDPSearchDataGenDao : ISDPSearchDataGenDao
    {
        readonly BaseDao baseDao = BaseDaoFactory.CreateBaseDao("AssembleDB");
        
        /// <summary>
        ///  插入SDPSearchDataGen
        /// </summary>
        /// <param name="sDPSearchDataGen">SDPSearchDataGen实体对象</param>
        /// <returns>状态代码</returns>
        public int InsertSDPSearchDataGen(SDPSearchDataGen sDPSearchDataGen)
        {
            //没有Insert相关SP,请检查数据库后重新生成。
            return 0;
        }
        
        /// <summary>
        /// 修改SDPSearchDataGen
        /// </summary>
        /// <param name="sDPSearchDataGen">SDPSearchDataGen实体对象</param>
        /// <returns>状态代码</returns>
        public int UpdateSDPSearchDataGen(SDPSearchDataGen sDPSearchDataGen)
        {
            //没有Update相关SP,请检查数据库后重新生成。
            return 0;
        }
        
        /// <summary>
        /// 删除SDPSearchDataGen
        /// </summary>
        /// <param name="sDPSearchDataGen">SDPSearchDataGen实体对象</param>
        /// <returns>状态代码</returns>
        public int DeleteSDPSearchDataGen(SDPSearchDataGen sDPSearchDataGen)
        {
            //没有Delete相关SP,请检查数据库后重新生成。
            return 0;
        }
        
        

        /// <summary>
        /// 根据主键获取SDPSearchDataGen信息
        /// </summary>
        /// <param name="searchId"></param>
        /// <returns>SDPSearchDataGen信息</returns>
        public SDPSearchDataGen FindByPk(long searchId )
        {
            try
            {
                return baseDao.GetByKey<SDPSearchDataGen>(searchId);
            }
            catch (Exception ex)
            {
                throw new DalException("调用SDPSearchDataGenDao时，访问FindByPk时出错", ex);
            }
        }

        /// <summary>
        /// 获取所有SDPSearchDataGen信息
        /// </summary>
        /// <returns>SDPSearchDataGen列表</returns>
        public IList<SDPSearchDataGen> GetAll()
        {
            try
            {
                return baseDao.GetAll<SDPSearchDataGen>();
            }
            catch (Exception ex)
            {
                throw new DalException("调用SDPSearchDataGenDao时，访问GetAll时出错", ex);
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
                String sql = "SELECT count(1) from SDP_SearchData  with (nolock)  ";

                object obj = baseDao.ExecScalar(sql);
                long ret = Convert.ToInt64(obj);
                return ret;
            }
            catch (Exception ex)
            {
                throw new DalException("调用SDPSearchDataGenDao时，访问Count时出错", ex);
            }
        }
        
        /// <summary>
        ///  检索SDPSearchDataGen，带翻页
        /// </summary>
        /// <param name="obj">SDPSearchDataGen实体对象检索条件</param>
        /// <param name="pagesize">每页记录数</param>
        /// <param name="pageNo">页码</param>
        /// <returns>检索结果</returns>
        public IList<SDPSearchDataGen> GetListByPage(SDPSearchDataGen obj, int pagesize, int pageNo)
        {
             try
            {
                var dic = new StatementParameterCollection();
                StringBuilder sbSql = new StringBuilder(200);

                
                 //计算ROWNUM
                int fromRownum = (pageNo - 1) * pagesize + 1;
                int endRownum = pagesize * pageNo;
                 sbSql.Append("WITH CTE AS ("); //WITH CTE 开始
                sbSql.Append("select row_number() over(order by SearchId desc ) as rownum, ");
                sbSql.Append(@"SearchId, SdpId, DistrictIds, DistrictNames, DistrictIdName, ScenicSpotIds, ScenicSpotNames, ScenicSpotIdName, MainScenicSpotScore, MainScenicSpotCommentCount, MainScenicSpotLatitude, MainScenicSpotLongitude, PeopleGroupNames, PeopleGroupIds, PeopleGroupIdName, ThemeNames, ThemeIds, ThemeIdName, HotelIds, HotelNames, HotelStarIds, HotelStarNames, HotelGifts, CtripRecommendScore, SalesCount, OnlineTime, DefaultCheapestPrice, ThreeStarCheapestPrice, FourStarCheapestPrice, FiveStarCheapestPrice, LowStarCheapestPrice, MobileDefaultCheapestPrice, MobileThreeStarCheapestPrice, MobileFourStarCheapestPrice, MobileFiveStarCheapestPrice, MobileLowStarCheapestPrice, Title, Description, Coupon, LastModifiedTime from SDP_SearchData (nolock) ");

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
                sbSql.Append(@"select SearchId, SdpId, DistrictIds, DistrictNames, DistrictIdName, ScenicSpotIds, ScenicSpotNames, ScenicSpotIdName, MainScenicSpotScore, MainScenicSpotCommentCount, MainScenicSpotLatitude, MainScenicSpotLongitude, PeopleGroupNames, PeopleGroupIds, PeopleGroupIdName, ThemeNames, ThemeIds, ThemeIdName, HotelIds, HotelNames, HotelStarIds, HotelStarNames, HotelGifts, CtripRecommendScore, SalesCount, OnlineTime, DefaultCheapestPrice, ThreeStarCheapestPrice, FourStarCheapestPrice, FiveStarCheapestPrice, LowStarCheapestPrice, MobileDefaultCheapestPrice, MobileThreeStarCheapestPrice, MobileFourStarCheapestPrice, MobileFiveStarCheapestPrice, MobileLowStarCheapestPrice, Title, Description, Coupon, LastModifiedTime from CTE Where rownum between @from and @end");
                dic.AddInParameter("@from", DbType.Int32, fromRownum);
                dic.AddInParameter("@end", DbType.Int32, endRownum);
                IList<SDPSearchDataGen> list = baseDao.SelectList<SDPSearchDataGen>(sbSql.ToString(), dic);

                return list;
            }
            catch (Exception ex)
            {
                throw new DalException("调用SDPSearchDataGenDao时，访问GetListByPage时出错", ex);
            }
        }






        
    }
}