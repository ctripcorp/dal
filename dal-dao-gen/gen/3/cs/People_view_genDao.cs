using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Text;
using Arch.Data;
using Arch.Data.DbEngine;
using com.ctrip.platform.uat.Entity.DataModel;
using com.ctrip.platform.uat.Interface.IDao;

namespace com.ctrip.platform.uat.Dao
{
   /// <summary>
    /// 更多DALFx接口功能，请参阅DALFx Confluence，地址：
    /// http://conf.ctripcorp.com/display/ARCH/Dal+Fx+API
    /// </summary>
    public partial class People_view_genDao : IPeople_view_genDao
    {
        readonly BaseDao baseDao = BaseDaoFactory.CreateBaseDao("HotelPubDB");
        
        /// <summary>
        ///  插入People_view_gen
        /// </summary>
        /// <param name="people_view_gen">People_view_gen实体对象</param>
        /// <returns>状态代码</returns>
        public int InsertPeople_view_gen(People_view_gen people_view_gen)
        {
            //没有Insert相关SP,请检查数据库后重新生成。
            return 0;
        }
        
        /*由于没有PK，不能生成Update和Delete方法
        /// <summary>
        /// 修改People_view_gen
        /// </summary>
        /// <param name="people_view_gen">People_view_gen实体对象</param>
        /// <returns>状态代码</returns>
        public int UpdatePeople_view_gen(People_view_gen people_view_gen)
        {
            //没有Update相关SP,请检查数据库后重新生成。
            return 0;
        }
        
        /// <summary>
        /// 删除People_view_gen
        /// </summary>
        /// <param name="people_view_gen">People_view_gen实体对象</param>
        /// <returns>状态代码</returns>
        public int DeletePeople_view_gen(People_view_gen people_view_gen)
        {
            //没有Delete相关SP,请检查数据库后重新生成。
            return 0;
        }
        
        
        */


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
        





        
    }
}
