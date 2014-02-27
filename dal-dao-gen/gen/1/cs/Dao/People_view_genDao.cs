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
    public partial class People_view_genDao : IPeople_view_genDao
    {
        readonly BaseDao baseDao = BaseDaoFactory.CreateBaseDao("HotelPubDB");
        

        /// <summary>
        /// 获取所有People_view_gen信息
        /// </summary>
        /// <returns>People_view_gen列表</returns>
        public IList<People_view_gen> GetAll()
        {
            try
            {
                return baseDao.GetAll<People_view_gen>();
            }
            catch (Exception ex)
            {
                throw new DalException("调用People_view_genDao时，访问GetAll时出错", ex);
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
                String sql = "SELECT count(1) from People_view  with (nolock)  ";

                object obj = baseDao.ExecScalar(sql);
                long ret = Convert.ToInt64(obj);
                return ret;
            }
            catch (Exception ex)
            {
                throw new DalException("调用People_view_genDao时，访问Count时出错", ex);
            }
        }
        
        /// <summary>
        ///  检索People_view_gen，带翻页
        /// </summary>
        /// <param name="obj">People_view_gen实体对象检索条件</param>
        /// <param name="pagesize">每页记录数</param>
        /// <param name="pageNo">页码</param>
        /// <returns>检索结果</returns>
        public IList<People_view_gen> GetListByPage(People_view_gen obj, int pagesize, int pageNo)
        {
             try
            {
                var dic = new StatementParameterCollection();
                StringBuilder sbSql = new StringBuilder(200);

                
                 //计算ROWNUM
                int fromRownum = (pageNo - 1) * pagesize + 1;
                int endRownum = pagesize * pageNo;
                 sbSql.Append("WITH CTE AS ("); //WITH CTE 开始
                sbSql.Append("select row_number() over(order by PeopleID desc ) as rownum, ");
                sbSql.Append(@"PeopleID, Name, CityID, ProvinceID, CountryID, CityName from People_view (nolock) ");

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
                sbSql.Append(@"select PeopleID, Name, CityID, ProvinceID, CountryID, CityName from CTE Where rownum between @from and @end");
                dic.AddInParameter("@from", DbType.Int32, fromRownum);
                dic.AddInParameter("@end", DbType.Int32, endRownum);
                IList<People_view_gen> list = baseDao.SelectList<People_view_gen>(sbSql.ToString(), dic);

                return list;
            }
            catch (Exception ex)
            {
                throw new DalException("调用People_view_genDao时，访问GetListByPage时出错", ex);
            }
        }






        
    }
}