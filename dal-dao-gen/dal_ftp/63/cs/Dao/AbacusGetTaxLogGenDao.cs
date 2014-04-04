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
    public partial class AbacusGetTaxLogGenDao : IAbacusGetTaxLogGenDao
    {
        readonly BaseDao baseDao = BaseDaoFactory.CreateBaseDao("AbacusDB_INSERT_1");
        
        /// <summary>
        ///  插入AbacusGetTaxLogGen
        /// </summary>
        /// <param name="abacusGetTaxLogGen">AbacusGetTaxLogGen实体对象</param>
        /// <returns>状态代码</returns>
        public int InsertAbacusGetTaxLogGen(AbacusGetTaxLogGen abacusGetTaxLogGen)
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter{ Name = "@LogID", Direction = ParameterDirection.InputOutput, DbType = DbType.Int32, Value = abacusGetTaxLogGen.LogID});
                parameters.Add(new StatementParameter{ Name = "@UID", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = abacusGetTaxLogGen.UID});
                parameters.Add(new StatementParameter{ Name = "@OrderID", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = abacusGetTaxLogGen.OrderID});
                parameters.Add(new StatementParameter{ Name = "@PNR", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = abacusGetTaxLogGen.PNR});
                parameters.Add(new StatementParameter{ Name = "@BeginTime", Direction = ParameterDirection.Input, DbType = DbType.DateTime, Value = abacusGetTaxLogGen.BeginTime});
                parameters.Add(new StatementParameter{ Name = "@EndTime", Direction = ParameterDirection.Input, DbType = DbType.DateTime, Value = abacusGetTaxLogGen.EndTime});
                parameters.Add(new StatementParameter{ Name = "@Result", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = abacusGetTaxLogGen.Result});
                parameters.Add(new StatementParameter{ Name = "@Tax", Direction = ParameterDirection.Input, DbType = DbType.Currency, Value = abacusGetTaxLogGen.Tax});
                parameters.Add(new StatementParameter{ Name = "@ErrDesc", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = abacusGetTaxLogGen.ErrDesc});
                parameters.Add(new StatementParameter{ Name = "@Source", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = abacusGetTaxLogGen.Source});
                parameters.Add(new StatementParameter{ Name = "@InsertTime", Direction = ParameterDirection.Input, DbType = DbType.DateTime, Value = abacusGetTaxLogGen.InsertTime});
                parameters.Add(new StatementParameter{ Name = "@PassengerType", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = abacusGetTaxLogGen.PassengerType});
                parameters.Add(new StatementParameter{ Name = "@OperatingAirline", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = abacusGetTaxLogGen.OperatingAirline});
                parameters.Add(new StatementParameter{ Name = "@DataChange_LastTime", Direction = ParameterDirection.Input, DbType = DbType.DateTime, Value = abacusGetTaxLogGen.DataChange_LastTime});
                parameters.Add(new StatementParameter{ Name = "@return",  Direction = ParameterDirection.ReturnValue});

                baseDao.ExecSp("spA_AbacusGetTaxLog_i", parameters);

               abacusGetTaxLogGen.LogID = (int)parameters["@LogID"].Value;
                return (int)parameters["@return"].Value;
            }
            catch (Exception ex)
            {
                throw new DalException("调用AbacusGetTaxLogGenDao时，访问Insert时出错", ex);
            }
        }
        
        /// <summary>
        /// 修改AbacusGetTaxLogGen
        /// </summary>
        /// <param name="abacusGetTaxLogGen">AbacusGetTaxLogGen实体对象</param>
        /// <returns>状态代码</returns>
        public int UpdateAbacusGetTaxLogGen(AbacusGetTaxLogGen abacusGetTaxLogGen)
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter{ Name = "@LogID", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = abacusGetTaxLogGen.LogID});
                parameters.Add(new StatementParameter{ Name = "@UID", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = abacusGetTaxLogGen.UID});
                parameters.Add(new StatementParameter{ Name = "@OrderID", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = abacusGetTaxLogGen.OrderID});
                parameters.Add(new StatementParameter{ Name = "@PNR", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = abacusGetTaxLogGen.PNR});
                parameters.Add(new StatementParameter{ Name = "@BeginTime", Direction = ParameterDirection.Input, DbType = DbType.DateTime, Value = abacusGetTaxLogGen.BeginTime});
                parameters.Add(new StatementParameter{ Name = "@EndTime", Direction = ParameterDirection.Input, DbType = DbType.DateTime, Value = abacusGetTaxLogGen.EndTime});
                parameters.Add(new StatementParameter{ Name = "@Result", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = abacusGetTaxLogGen.Result});
                parameters.Add(new StatementParameter{ Name = "@Tax", Direction = ParameterDirection.Input, DbType = DbType.Currency, Value = abacusGetTaxLogGen.Tax});
                parameters.Add(new StatementParameter{ Name = "@ErrDesc", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = abacusGetTaxLogGen.ErrDesc});
                parameters.Add(new StatementParameter{ Name = "@Source", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = abacusGetTaxLogGen.Source});
                parameters.Add(new StatementParameter{ Name = "@InsertTime", Direction = ParameterDirection.Input, DbType = DbType.DateTime, Value = abacusGetTaxLogGen.InsertTime});
                parameters.Add(new StatementParameter{ Name = "@PassengerType", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = abacusGetTaxLogGen.PassengerType});
                parameters.Add(new StatementParameter{ Name = "@OperatingAirline", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = abacusGetTaxLogGen.OperatingAirline});
                parameters.Add(new StatementParameter{ Name = "@DataChange_LastTime", Direction = ParameterDirection.Input, DbType = DbType.DateTime, Value = abacusGetTaxLogGen.DataChange_LastTime});
                parameters.Add(new StatementParameter{ Name = "@return",  Direction = ParameterDirection.ReturnValue});

                baseDao.ExecSp("spA_AbacusGetTaxLog_u", parameters);

                return (int)parameters["@return"].Value;
            }
            catch (Exception ex)
            {
                throw new DalException("调用AbacusGetTaxLogGenDao时，访问Update时出错", ex);
            }
        }
        
        /// <summary>
        /// 删除AbacusGetTaxLogGen
        /// </summary>
        /// <param name="abacusGetTaxLogGen">AbacusGetTaxLogGen实体对象</param>
        /// <returns>状态代码</returns>
        public int DeleteAbacusGetTaxLogGen(AbacusGetTaxLogGen abacusGetTaxLogGen)
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter{ Name = "@LogID", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = abacusGetTaxLogGen.LogID});
                parameters.Add(new StatementParameter{ Name = "@return",  Direction = ParameterDirection.ReturnValue});

                baseDao.ExecSp("spA_AbacusGetTaxLog_d", parameters);

                return (int)parameters["@return"].Value;
            }
            catch (Exception ex)
            {
                throw new DalException("调用AbacusGetTaxLogGenDao时，访问Delete时出错", ex);
            }
        }
        
        /// <summary>
        /// 删除AbacusGetTaxLogGen
        /// </summary>
        /// <param name="logID">@LogID #></param>
        /// <returns>状态代码</returns>
        public int DeleteAbacusGetTaxLogGen(int logID)
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter{ Name = "@LogID", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = logID});
                parameters.Add(new StatementParameter{ Name = "@return",  Direction = ParameterDirection.ReturnValue});

                baseDao.ExecSp("spA_AbacusGetTaxLog_d", parameters);

                return (int)parameters["@return"].Value;
            }
            catch (Exception ex)
            {
                throw new DalException("调用AbacusGetTaxLogGenDao时，访问DeleteAbacusGetTaxLogGen时出错", ex);
            }
        }
        

        /// <summary>
        /// 根据主键获取AbacusGetTaxLogGen信息
        /// </summary>
        /// <param name="logID"></param>
        /// <returns>AbacusGetTaxLogGen信息</returns>
        public AbacusGetTaxLogGen FindByPk(int logID )
        {
            try
            {
                return baseDao.GetByKey<AbacusGetTaxLogGen>(logID);
            }
            catch (Exception ex)
            {
                throw new DalException("调用AbacusGetTaxLogGenDao时，访问FindByPk时出错", ex);
            }
        }

        /// <summary>
        /// 获取所有AbacusGetTaxLogGen信息
        /// </summary>
        /// <returns>AbacusGetTaxLogGen列表</returns>
        public IList<AbacusGetTaxLogGen> GetAll()
        {
            try
            {
                return baseDao.GetAll<AbacusGetTaxLogGen>();
            }
            catch (Exception ex)
            {
                throw new DalException("调用AbacusGetTaxLogGenDao时，访问GetAll时出错", ex);
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
                String sql = "SELECT count(1) from AbacusGetTaxLog  with (nolock)  ";

                object obj = baseDao.ExecScalar(sql);
                long ret = Convert.ToInt64(obj);
                return ret;
            }
            catch (Exception ex)
            {
                throw new DalException("调用AbacusGetTaxLogGenDao时，访问Count时出错", ex);
            }
        }
        
        /// <summary>
        ///  检索AbacusGetTaxLogGen，带翻页
        /// </summary>
        /// <param name="obj">AbacusGetTaxLogGen实体对象检索条件</param>
        /// <param name="pagesize">每页记录数</param>
        /// <param name="pageNo">页码</param>
        /// <returns>检索结果</returns>
        public IList<AbacusGetTaxLogGen> GetListByPage(AbacusGetTaxLogGen obj, int pagesize, int pageNo)
        {
             try
            {
                var dic = new StatementParameterCollection();
                StringBuilder sbSql = new StringBuilder(200);

                
                 //计算ROWNUM
                int fromRownum = (pageNo - 1) * pagesize + 1;
                int endRownum = pagesize * pageNo;
                 sbSql.Append("WITH CTE AS ("); //WITH CTE 开始
                sbSql.Append("select row_number() over(order by LogID desc ) as rownum, ");
                sbSql.Append(@"LogID, UID, OrderID, PNR, BeginTime, EndTime, Result, Tax, ErrDesc, Source, InsertTime, PassengerType, OperatingAirline, DataChangeLastTime from AbacusGetTaxLog (nolock) ");

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
                sbSql.Append(@"select LogID, UID, OrderID, PNR, BeginTime, EndTime, Result, Tax, ErrDesc, Source, InsertTime, PassengerType, OperatingAirline, DataChangeLastTime from CTE Where rownum between @from and @end");
                dic.AddInParameter("@from", DbType.Int32, fromRownum);
                dic.AddInParameter("@end", DbType.Int32, endRownum);
                IList<AbacusGetTaxLogGen> list = baseDao.SelectList<AbacusGetTaxLogGen>(sbSql.ToString(), dic);

                return list;
            }
            catch (Exception ex)
            {
                throw new DalException("调用AbacusGetTaxLogGenDao时，访问GetListByPage时出错", ex);
            }
        }






        
    }
}
