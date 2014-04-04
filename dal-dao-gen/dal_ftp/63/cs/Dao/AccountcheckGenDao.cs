using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Text;
using Arch.Data;
using Arch.Data.DbEngine;
using com.ctrip.dal.test.test4.Entity.DataModel;
using com.ctrip.dal.test.test4.Interface.IDao;

namespace com.ctrip.dal.test.test4.Dao
{
   /// <summary>
    /// 更多DAL接口功能，请参阅DAL Confluence，地址：
    /// http://conf.ctripcorp.com/display/SysDev/Dal+Fx+API
    /// </summary>
    public partial class AccountcheckGenDao : IAccountcheckGenDao
    {
        readonly BaseDao baseDao = BaseDaoFactory.CreateBaseDao("AccCorpDB_SELECT_1");
        
        /// <summary>
        ///  插入AccountcheckGen
        /// </summary>
        /// <param name="accountcheckGen">AccountcheckGen实体对象</param>
        /// <returns>新增的主键</returns>
        public int InsertAccountcheckGen(AccountcheckGen accountcheckGen)
        {
            try
            {
                Object result = baseDao.Insert<AccountcheckGen>(accountcheckGen);
                int iReturn = Convert.ToInt32(result);

                return iReturn;
            }
            catch (Exception ex)
            {
                throw new DalException("调用AccountcheckGen时，访问Insert时出错", ex);
            }
        }
        
        /*由于没有PK，不能生成Update和Delete方法
        /// <summary>
        /// 修改AccountcheckGen
        /// </summary>
        /// <param name="accountcheckGen">AccountcheckGen实体对象</param>
        /// <returns>状态代码</returns>
        public int UpdateAccountcheckGen(AccountcheckGen accountcheckGen)
        {
            try
            {
                Object result = baseDao.Update<AccountcheckGen>(accountcheckGen);
                int iReturn = Convert.ToInt32(result);

                return iReturn;
            }
            catch (Exception ex)
            {
                throw new DalException("调用AccountcheckGen时，访问Update时出错", ex);
            }
        }
        
        /// <summary>
        /// 删除AccountcheckGen
        /// </summary>
        /// <param name="accountcheckGen">AccountcheckGen实体对象</param>
        /// <returns>状态代码</returns>
        public int DeleteAccountcheckGen(AccountcheckGen accountcheckGen)
        {
            try
            {
                Object result = baseDao.Delete<AccountcheckGen>(accountcheckGen);
                int iReturn = Convert.ToInt32(result);

                return iReturn;
            }
            catch (Exception ex)
            {
                throw new DalException("调用AccountcheckGen时，访问Delete时出错", ex);
            }
        }
        
        
        */


        /// <summary>
        /// 获取所有AccountcheckGen信息
        /// </summary>
        /// <returns>AccountcheckGen列表</returns>
        public IList<AccountcheckGen> GetAll()
        {
            try
            {
                return baseDao.GetAll<AccountcheckGen>();
            }
            catch (Exception ex)
            {
                throw new DalException("调用AccountcheckGenDao时，访问GetAll时出错", ex);
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
                String sql = "SELECT count(1) from _accountcheck  with (nolock)  ";

                object obj = baseDao.ExecScalar(sql);
                long ret = Convert.ToInt64(obj);
                return ret;
            }
            catch (Exception ex)
            {
                throw new DalException("调用AccountcheckGenDao时，访问Count时出错", ex);
            }
        }
        






		/// <summary>
        ///  fffff
        /// </summary>
        /// <param name="limited"></param>
        /// <returns></returns>
        public IList<AccountcheckGen> fffff(decimal limited)
        {
        	try
            {
            	string sql = "SELECT * FROM _accountcheck WHERE  limited != ? ";
                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter{ Name = "@limited", Direction = ParameterDirection.Input, DbType = DbType.Currency, Value =limited });

                return baseDao.SelectList<AccountcheckGen>(sql, parameters);

            }
            catch (Exception ex)
            {
                throw new DalException("调用AccountcheckGenDao时，访问fffff时出错", ex);
            }
        }
        
    }
}
