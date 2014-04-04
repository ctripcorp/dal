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
    public partial class AccountcheckDao : IAccountcheckDao
    {
        readonly BaseDao baseDao = BaseDaoFactory.CreateBaseDao("AccCorpDB_INSERT_1");
        
        /// <summary>
        ///  插入Accountcheck
        /// </summary>
        /// <param name="accountcheck">Accountcheck实体对象</param>
        /// <returns>状态代码</returns>
        public int InsertAccountcheck(Accountcheck accountcheck)
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter{ Name = "@AccCheckID", Direction = ParameterDirection.InputOutput, DbType = DbType.Int32, Value = accountcheck.AccCheckID});
                parameters.Add(new StatementParameter{ Name = "@CorpID", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = accountcheck.CorpID});
                parameters.Add(new StatementParameter{ Name = "@BatchNo", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = accountcheck.BatchNo});
                parameters.Add(new StatementParameter{ Name = "@AccountID", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = accountcheck.AccountID});
                parameters.Add(new StatementParameter{ Name = "@SubAccountID", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = accountcheck.SubAccountID});
                parameters.Add(new StatementParameter{ Name = "@BatchStatus", Direction = ParameterDirection.Input, DbType = DbType.AnsiStringFixedLength, Value = accountcheck.BatchStatus});
                parameters.Add(new StatementParameter{ Name = "@AccBalanceID", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = accountcheck.AccBalanceID});
                parameters.Add(new StatementParameter{ Name = "@AccountType", Direction = ParameterDirection.Input, DbType = DbType.AnsiStringFixedLength, Value = accountcheck.AccountType});
                parameters.Add(new StatementParameter{ Name = "@CheckAccType", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = accountcheck.CheckAccType});
                parameters.Add(new StatementParameter{ Name = "@Operator", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = accountcheck.Operator});
                parameters.Add(new StatementParameter{ Name = "@ModifyTime", Direction = ParameterDirection.Input, DbType = DbType.DateTime, Value = accountcheck.ModifyTime});
                parameters.Add(new StatementParameter{ Name = "@StartDate", Direction = ParameterDirection.Input, DbType = DbType.AnsiStringFixedLength, Value = accountcheck.StartDate});
                parameters.Add(new StatementParameter{ Name = "@EndDate", Direction = ParameterDirection.Input, DbType = DbType.AnsiStringFixedLength, Value = accountcheck.EndDate});
                parameters.Add(new StatementParameter{ Name = "@FltconMoney", Direction = ParameterDirection.Input, DbType = DbType.Currency, Value = accountcheck.FltconMoney});
                parameters.Add(new StatementParameter{ Name = "@HtlHconMoney", Direction = ParameterDirection.Input, DbType = DbType.Currency, Value = accountcheck.HtlHconMoney});
                parameters.Add(new StatementParameter{ Name = "@HtlXconMoney", Direction = ParameterDirection.Input, DbType = DbType.Currency, Value = accountcheck.HtlXconMoney});
                parameters.Add(new StatementParameter{ Name = "@limited", Direction = ParameterDirection.Input, DbType = DbType.Currency, Value = accountcheck.Limited});
                parameters.Add(new StatementParameter{ Name = "@LimitedTemp", Direction = ParameterDirection.Input, DbType = DbType.Currency, Value = accountcheck.LimitedTemp});
                parameters.Add(new StatementParameter{ Name = "@return",  Direction = ParameterDirection.ReturnValue});

                baseDao.ExecSp("spA__accountcheck_i", parameters);

               accountcheck.AccCheckID = (int)parameters["@AccCheckID"].Value;
                return (int)parameters["@return"].Value;
            }
            catch (Exception ex)
            {
                throw new DalException("调用AccountcheckDao时，访问Insert时出错", ex);
            }
        }
        
        /*由于没有PK，不能生成Update和Delete方法
        /// <summary>
        /// 修改Accountcheck
        /// </summary>
        /// <param name="accountcheck">Accountcheck实体对象</param>
        /// <returns>状态代码</returns>
        public int UpdateAccountcheck(Accountcheck accountcheck)
        {
            //没有Update相关SP,请检查数据库后重新生成。
            return 0;
        }
        
        /// <summary>
        /// 删除Accountcheck
        /// </summary>
        /// <param name="accountcheck">Accountcheck实体对象</param>
        /// <returns>状态代码</returns>
        public int DeleteAccountcheck(Accountcheck accountcheck)
        {
            //没有Delete相关SP,请检查数据库后重新生成。
            return 0;
        }
        
        
        */


        /// <summary>
        /// 获取所有Accountcheck信息
        /// </summary>
        /// <returns>Accountcheck列表</returns>
        public IList<Accountcheck> GetAll()
        {
            try
            {
                return baseDao.GetAll<Accountcheck>();
            }
            catch (Exception ex)
            {
                throw new DalException("调用AccountcheckDao时，访问GetAll时出错", ex);
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
                throw new DalException("调用AccountcheckDao时，访问Count时出错", ex);
            }
        }
        
        /// <summary>
        ///  检索Accountcheck，带翻页
        /// </summary>
        /// <param name="obj">Accountcheck实体对象检索条件</param>
        /// <param name="pagesize">每页记录数</param>
        /// <param name="pageNo">页码</param>
        /// <returns>检索结果</returns>
        public IList<Accountcheck> GetListByPage(Accountcheck obj, int pagesize, int pageNo)
        {
             try
            {
                var dic = new StatementParameterCollection();
                StringBuilder sbSql = new StringBuilder(200);

                
                 //计算ROWNUM
                int fromRownum = (pageNo - 1) * pagesize + 1;
                int endRownum = pagesize * pageNo;
                 sbSql.Append("WITH CTE AS ("); //WITH CTE 开始
                sbSql.Append("select row_number() over(order by AccCheckID desc ) as rownum, ");
                sbSql.Append(@"AccCheckID, CorpID, BatchNo, AccountID, SubAccountID, BatchStatus, AccBalanceID, AccountType, CheckAccType, Operator, ModifyTime, StartDate, EndDate, FltconMoney, HtlHconMoney, HtlXconMoney, limited, LimitedTemp, DataChangeLastTime from _accountcheck (nolock) ");

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
                sbSql.Append(@"select AccCheckID, CorpID, BatchNo, AccountID, SubAccountID, BatchStatus, AccBalanceID, AccountType, CheckAccType, Operator, ModifyTime, StartDate, EndDate, FltconMoney, HtlHconMoney, HtlXconMoney, limited, LimitedTemp, DataChangeLastTime from CTE Where rownum between @from and @end");
                dic.AddInParameter("@from", DbType.Int32, fromRownum);
                dic.AddInParameter("@end", DbType.Int32, endRownum);
                IList<Accountcheck> list = baseDao.SelectList<Accountcheck>(sbSql.ToString(), dic);

                return list;
            }
            catch (Exception ex)
            {
                throw new DalException("调用AccountcheckDao时，访问GetListByPage时出错", ex);
            }
        }






        
    }
}
