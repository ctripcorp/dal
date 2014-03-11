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
    public partial class MoneyTestGenDao : IMoneyTestGenDao
    {
        readonly BaseDao baseDao = BaseDaoFactory.CreateBaseDao("dao_test");
        
        /// <summary>
        ///  插入MoneyTestGen
        /// </summary>
        /// <param name="moneyTestGen">MoneyTestGen实体对象</param>
        /// <returns>新增的主键</returns>
        public int InsertMoneyTestGen(MoneyTestGen moneyTestGen)
        {
            try
            {
                Object result = baseDao.Insert<MoneyTestGen>(moneyTestGen);
                int iReturn = Convert.ToInt32(result);

                return iReturn;
            }
            catch (Exception ex)
            {
                throw new DalException("调用MoneyTestGen时，访问Insert时出错", ex);
            }
        }
        
        /// <summary>
        /// 修改MoneyTestGen
        /// </summary>
        /// <param name="moneyTestGen">MoneyTestGen实体对象</param>
        /// <returns>状态代码</returns>
        public int UpdateMoneyTestGen(MoneyTestGen moneyTestGen)
        {
            try
            {
                Object result = baseDao.Update<MoneyTestGen>(moneyTestGen);
                int iReturn = Convert.ToInt32(result);

                return iReturn;
            }
            catch (Exception ex)
            {
                throw new DalException("调用MoneyTestGen时，访问Update时出错", ex);
            }
        }
        
        /// <summary>
        /// 删除MoneyTestGen
        /// </summary>
        /// <param name="moneyTestGen">MoneyTestGen实体对象</param>
        /// <returns>状态代码</returns>
        public int DeleteMoneyTestGen(MoneyTestGen moneyTestGen)
        {
            try
            {
                Object result = baseDao.Delete<MoneyTestGen>(moneyTestGen);
                int iReturn = Convert.ToInt32(result);

                return iReturn;
            }
            catch (Exception ex)
            {
                throw new DalException("调用MoneyTestGen时，访问Delete时出错", ex);
            }
        }
        
        

        /// <summary>
        /// 根据主键获取MoneyTestGen信息
        /// </summary>
        /// <param name="id"></param>
        /// <returns>MoneyTestGen信息</returns>
        public MoneyTestGen FindByPk(int id )
        {
            try
            {
                return baseDao.GetByKey<MoneyTestGen>(id);
            }
            catch (Exception ex)
            {
                throw new DalException("调用MoneyTestGenDao时，访问FindByPk时出错", ex);
            }
        }

        /// <summary>
        /// 获取所有MoneyTestGen信息
        /// </summary>
        /// <returns>MoneyTestGen列表</returns>
        public IList<MoneyTestGen> GetAll()
        {
            try
            {
                return baseDao.GetAll<MoneyTestGen>();
            }
            catch (Exception ex)
            {
                throw new DalException("调用MoneyTestGenDao时，访问GetAll时出错", ex);
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
                String sql = "SELECT count(1) from MoneyTest    ";

                object obj = baseDao.ExecScalar(sql);
                long ret = Convert.ToInt64(obj);
                return ret;
            }
            catch (Exception ex)
            {
                throw new DalException("调用MoneyTestGenDao时，访问Count时出错", ex);
            }
        }
        
        /// <summary>
        ///  检索MoneyTestGen，带翻页
        /// </summary>
        /// <param name="obj">MoneyTestGen实体对象检索条件</param>
        /// <param name="pagesize">每页记录数</param>
        /// <param name="pageNo">页码</param>
        /// <returns>检索结果</returns>
        public IList<MoneyTestGen> GetListByPage(MoneyTestGen obj, int pagesize, int pageNo)
        {
             try
            {
                var dic = new StatementParameterCollection();
                StringBuilder sbSql = new StringBuilder(200);

                
                sbSql.Append(@"select id, money_all, bool_test, date_test from MoneyTest ");
                //包含查询条件
                //StringBuilder whereCondition = new StringBuilder();
                //if (!string.IsNullOrEmpty(obj.Name))
                //{
                //    //人名
                //    whereCondition.Append("Where Name like @Name ");
                //    dic.AddInParameter("@Name", DbType.String, "%" + obj.Name + "%");
                //}
                //sbSql.Append(whereCondition);
                sbSql.Append(" order by id desc ");
                sbSql.Append( string.Format("limit {0}, {1} ", (pageNo - 1) * pagesize, pagesize));
                IList<MoneyTestGen> list = baseDao.SelectList<MoneyTestGen>(sbSql.ToString(), dic);

                return list;
            }
            catch (Exception ex)
            {
                throw new DalException("调用MoneyTestGenDao时，访问GetListByPage时出错", ex);
            }
        }






        
    }
}