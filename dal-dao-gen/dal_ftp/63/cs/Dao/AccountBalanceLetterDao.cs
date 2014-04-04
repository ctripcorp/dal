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
    public partial class AccountBalanceLetterDao : IAccountBalanceLetterDao
    {
        readonly BaseDao baseDao = BaseDaoFactory.CreateBaseDao("AccCorpDB_INSERT_1");
        
        /// <summary>
        ///  插入AccountBalanceLetter
        /// </summary>
        /// <param name="accountBalanceLetter">AccountBalanceLetter实体对象</param>
        /// <returns>状态代码</returns>
        public int InsertAccountBalanceLetter(AccountBalanceLetter accountBalanceLetter)
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter{ Name = "@RecordId", Direction = ParameterDirection.InputOutput, DbType = DbType.Int32, Value = accountBalanceLetter.RecordId});
                parameters.Add(new StatementParameter{ Name = "@AccBalanceId", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = accountBalanceLetter.AccBalanceId});
                parameters.Add(new StatementParameter{ Name = "@FileName", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = accountBalanceLetter.FileName});
                parameters.Add(new StatementParameter{ Name = "@UploadPerson", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = accountBalanceLetter.UploadPerson});
                parameters.Add(new StatementParameter{ Name = "@DescribeInfo", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = accountBalanceLetter.DescribeInfo});
                parameters.Add(new StatementParameter{ Name = "@CreateTime", Direction = ParameterDirection.Input, DbType = DbType.DateTime, Value = accountBalanceLetter.CreateTime});
                parameters.Add(new StatementParameter{ Name = "@DataChange_LastTime", Direction = ParameterDirection.Input, DbType = DbType.DateTime, Value = accountBalanceLetter.DataChange_LastTime});
                parameters.Add(new StatementParameter{ Name = "@return",  Direction = ParameterDirection.ReturnValue});

                baseDao.ExecSp("spA_AccountBalanceLetter_i", parameters);

               accountBalanceLetter.RecordId = (int)parameters["@RecordId"].Value;
                return (int)parameters["@return"].Value;
            }
            catch (Exception ex)
            {
                throw new DalException("调用AccountBalanceLetterDao时，访问Insert时出错", ex);
            }
        }
        
        /// <summary>
        /// 修改AccountBalanceLetter
        /// </summary>
        /// <param name="accountBalanceLetter">AccountBalanceLetter实体对象</param>
        /// <returns>状态代码</returns>
        public int UpdateAccountBalanceLetter(AccountBalanceLetter accountBalanceLetter)
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter{ Name = "@RecordId", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = accountBalanceLetter.RecordId});
                parameters.Add(new StatementParameter{ Name = "@AccBalanceId", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = accountBalanceLetter.AccBalanceId});
                parameters.Add(new StatementParameter{ Name = "@FileName", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = accountBalanceLetter.FileName});
                parameters.Add(new StatementParameter{ Name = "@UploadPerson", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = accountBalanceLetter.UploadPerson});
                parameters.Add(new StatementParameter{ Name = "@DescribeInfo", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = accountBalanceLetter.DescribeInfo});
                parameters.Add(new StatementParameter{ Name = "@CreateTime", Direction = ParameterDirection.Input, DbType = DbType.DateTime, Value = accountBalanceLetter.CreateTime});
                parameters.Add(new StatementParameter{ Name = "@DataChange_LastTime", Direction = ParameterDirection.Input, DbType = DbType.DateTime, Value = accountBalanceLetter.DataChange_LastTime});
                parameters.Add(new StatementParameter{ Name = "@return",  Direction = ParameterDirection.ReturnValue});

                baseDao.ExecSp("spA_AccountBalanceLetter_u", parameters);

                return (int)parameters["@return"].Value;
            }
            catch (Exception ex)
            {
                throw new DalException("调用AccountBalanceLetterDao时，访问Update时出错", ex);
            }
        }
        
        /// <summary>
        /// 删除AccountBalanceLetter
        /// </summary>
        /// <param name="accountBalanceLetter">AccountBalanceLetter实体对象</param>
        /// <returns>状态代码</returns>
        public int DeleteAccountBalanceLetter(AccountBalanceLetter accountBalanceLetter)
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter{ Name = "@RecordId", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = accountBalanceLetter.RecordId});
                parameters.Add(new StatementParameter{ Name = "@return",  Direction = ParameterDirection.ReturnValue});

                baseDao.ExecSp("spA_AccountBalanceLetter_d", parameters);

                return (int)parameters["@return"].Value;
            }
            catch (Exception ex)
            {
                throw new DalException("调用AccountBalanceLetterDao时，访问Delete时出错", ex);
            }
        }
        
        /// <summary>
        /// 删除AccountBalanceLetter
        /// </summary>
        /// <param name="recordId">@RecordId #></param>
        /// <returns>状态代码</returns>
        public int DeleteAccountBalanceLetter(int recordId)
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter{ Name = "@RecordId", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = recordId});
                parameters.Add(new StatementParameter{ Name = "@return",  Direction = ParameterDirection.ReturnValue});

                baseDao.ExecSp("spA_AccountBalanceLetter_d", parameters);

                return (int)parameters["@return"].Value;
            }
            catch (Exception ex)
            {
                throw new DalException("调用AccountBalanceLetterDao时，访问DeleteAccountBalanceLetter时出错", ex);
            }
        }
        

        /// <summary>
        /// 根据主键获取AccountBalanceLetter信息
        /// </summary>
        /// <param name="recordId"></param>
        /// <returns>AccountBalanceLetter信息</returns>
        public AccountBalanceLetter FindByPk(int recordId )
        {
            try
            {
                return baseDao.GetByKey<AccountBalanceLetter>(recordId);
            }
            catch (Exception ex)
            {
                throw new DalException("调用AccountBalanceLetterDao时，访问FindByPk时出错", ex);
            }
        }

        /// <summary>
        /// 获取所有AccountBalanceLetter信息
        /// </summary>
        /// <returns>AccountBalanceLetter列表</returns>
        public IList<AccountBalanceLetter> GetAll()
        {
            try
            {
                return baseDao.GetAll<AccountBalanceLetter>();
            }
            catch (Exception ex)
            {
                throw new DalException("调用AccountBalanceLetterDao时，访问GetAll时出错", ex);
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
                String sql = "SELECT count(1) from AccountBalanceLetter  with (nolock)  ";

                object obj = baseDao.ExecScalar(sql);
                long ret = Convert.ToInt64(obj);
                return ret;
            }
            catch (Exception ex)
            {
                throw new DalException("调用AccountBalanceLetterDao时，访问Count时出错", ex);
            }
        }
        
        /// <summary>
        ///  检索AccountBalanceLetter，带翻页
        /// </summary>
        /// <param name="obj">AccountBalanceLetter实体对象检索条件</param>
        /// <param name="pagesize">每页记录数</param>
        /// <param name="pageNo">页码</param>
        /// <returns>检索结果</returns>
        public IList<AccountBalanceLetter> GetListByPage(AccountBalanceLetter obj, int pagesize, int pageNo)
        {
             try
            {
                var dic = new StatementParameterCollection();
                StringBuilder sbSql = new StringBuilder(200);

                
                 //计算ROWNUM
                int fromRownum = (pageNo - 1) * pagesize + 1;
                int endRownum = pagesize * pageNo;
                 sbSql.Append("WITH CTE AS ("); //WITH CTE 开始
                sbSql.Append("select row_number() over(order by RecordId desc ) as rownum, ");
                sbSql.Append(@"RecordId, AccBalanceId, FileName, UploadPerson, DescribeInfo, CreateTime, DataChangeLastTime from AccountBalanceLetter (nolock) ");

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
                sbSql.Append(@"select RecordId, AccBalanceId, FileName, UploadPerson, DescribeInfo, CreateTime, DataChangeLastTime from CTE Where rownum between @from and @end");
                dic.AddInParameter("@from", DbType.Int32, fromRownum);
                dic.AddInParameter("@end", DbType.Int32, endRownum);
                IList<AccountBalanceLetter> list = baseDao.SelectList<AccountBalanceLetter>(sbSql.ToString(), dic);

                return list;
            }
            catch (Exception ex)
            {
                throw new DalException("调用AccountBalanceLetterDao时，访问GetListByPage时出错", ex);
            }
        }






        
    }
}
