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
        readonly BaseDao baseDao = BaseDaoFactory.CreateBaseDao("dao_test");
        
        /// <summary>
        ///  插入MoneyTest_gen
        /// </summary>
        /// <param name="moneyTest_gen">MoneyTest_gen实体对象</param>
        /// <returns>状态代码</returns>
        public int InsertMoneyTest_gen(MoneyTest_gen moneyTest_gen)
        {
            //没有Insert相关SP,请检查数据库后重新生成。
            return 0;
        }
        
        /// <summary>
        /// 修改MoneyTest_gen
        /// </summary>
        /// <param name="moneyTest_gen">MoneyTest_gen实体对象</param>
        /// <returns>状态代码</returns>
        public int UpdateMoneyTest_gen(MoneyTest_gen moneyTest_gen)
        {
            //没有Update相关SP,请检查数据库后重新生成。
            return 0;
        }
        
        /// <summary>
        /// 删除MoneyTest_gen
        /// </summary>
        /// <param name="moneyTest_gen">MoneyTest_gen实体对象</param>
        /// <returns>状态代码</returns>
        public int DeleteMoneyTest_gen(MoneyTest_gen moneyTest_gen)
        {
            //没有Delete相关SP,请检查数据库后重新生成。
            return 0;
        }
        
        

        /// <summary>
        /// 根据主键获取MoneyTest_gen信息
        /// </summary>
        /// <param name="id"></param>
        /// <returns>MoneyTest_gen信息</returns>
        public MoneyTest_gen FindByPk(int id )
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
                String sql = "SELECT count(1) from MoneyTest    ";

                object obj = baseDao.ExecScalar(sql);
                long ret = Convert.ToInt64(obj);
                return ret;
            }
            catch (Exception ex)
            {
                throw new DalException("调用MoneyTest_genDao时，访问Count时出错", ex);
            }
        }
        
        /// <summary>
        ///  检索MoneyTest_gen，带翻页
        /// </summary>
        /// <param name="obj">MoneyTest_gen实体对象检索条件</param>
        /// <param name="pagesize">每页记录数</param>
        /// <param name="pageNo">页码</param>
        /// <returns>检索结果</returns>
        public IList<MoneyTest_gen> GetListByPage(MoneyTest_gen obj, int pagesize, int pageNo)
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
                IList<MoneyTest_gen> list = baseDao.SelectList<MoneyTest_gen>(sbSql.ToString(), dic);

                return list;
            }
            catch (Exception ex)
            {
                throw new DalException("调用MoneyTest_genDao时，访问GetListByPage时出错", ex);
            }
        }






        
    }
}