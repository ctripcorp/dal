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
    public partial class MoneyTest_genDao : IMoneyTest_genDao
    {
        readonly BaseDao baseDao = BaseDaoFactory.CreateBaseDao("PerformanceTest");
        
        /// <summary>
        ///  插入MoneyTest_gen
        /// </summary>
        /// <param name="moneyTest_gen">MoneyTest_gen实体对象</param>
        /// <returns>新增的主键</returns>
        public int InsertMoneyTest_gen(MoneyTest_gen moneyTest_gen)
        {
            try
            {
                Object result = baseDao.Insert<MoneyTest_gen>(moneyTest_gen);
                int iReturn = Convert.ToInt32(result);

                return iReturn;
            }
            catch (Exception ex)
            {
                throw new DalException("调用MoneyTest_gen时，访问Insert时出错", ex);
            }
        }
        
        /// <summary>
        /// 修改MoneyTest_gen
        /// </summary>
        /// <param name="moneyTest_gen">MoneyTest_gen实体对象</param>
        /// <returns>状态代码</returns>
        public int UpdateMoneyTest_gen(MoneyTest_gen moneyTest_gen)
        {
            try
            {
                Object result = baseDao.Update<MoneyTest_gen>(moneyTest_gen);
                int iReturn = Convert.ToInt32(result);

                return iReturn;
            }
            catch (Exception ex)
            {
                throw new DalException("调用MoneyTest_gen时，访问Update时出错", ex);
            }
        }
        
        /// <summary>
        /// 删除MoneyTest_gen
        /// </summary>
        /// <param name="moneyTest_gen">MoneyTest_gen实体对象</param>
        /// <returns>状态代码</returns>
        public int DeleteMoneyTest_gen(MoneyTest_gen moneyTest_gen)
        {
            try
            {
                Object result = baseDao.Delete<MoneyTest_gen>(moneyTest_gen);
                int iReturn = Convert.ToInt32(result);

                return iReturn;
            }
            catch (Exception ex)
            {
                throw new DalException("调用MoneyTest_gen时，访问Delete时出错", ex);
            }
        }
        
        

        /// <summary>
        /// 根据主键获取MoneyTest_gen信息
        /// </summary>
        /// <param name="id"></param>
        /// <returns>MoneyTest_gen信息</returns>
        public MoneyTest_gen FindByPk(uint id )
        {
            try
            {
                return baseDao.GetByKey<MoneyTest_gen>(id);
            }
            catch (Exception ex)
            {
                throw new DalException("调用MoneyTest_genDao时，访问FindByPk时出错", ex);
            }
        }

        /// <summary>
        /// 获取所有MoneyTest_gen信息
        /// </summary>
        /// <returns>MoneyTest_gen列表</returns>
        public IList<MoneyTest_gen> GetAll()
        {
            try
            {
                return baseDao.GetAll<MoneyTest_gen>();
            }
            catch (Exception ex)
            {
                throw new DalException("调用MoneyTest_genDao时，访问GetAll时出错", ex);
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
                String sql = "SELECT count(1) from MoneyTest  with (nolock)  ";

                object obj = baseDao.ExecScalar(sql);
                long ret = Convert.ToInt64(obj);
                return ret;
            }
            catch (Exception ex)
            {
                throw new DalException("调用MoneyTest_genDao时，访问Count时出错", ex);
            }
        }
        






        
    }
}