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
    public partial class FltOrdersTmpDao : IFltOrdersTmpDao
    {
        readonly BaseDao baseDao = BaseDaoFactory.CreateBaseDao("AccCorpDB_INSERT_1");
        
        /// <summary>
        ///  插入FltOrdersTmp
        /// </summary>
        /// <param name="fltOrdersTmp">FltOrdersTmp实体对象</param>
        /// <returns>状态代码</returns>
        public int InsertFltOrdersTmp(FltOrdersTmp fltOrdersTmp)
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter{ Name = "@RecordId", Direction = ParameterDirection.InputOutput, DbType = DbType.Int32, Value = fltOrdersTmp.RecordId});
                parameters.Add(new StatementParameter{ Name = "@OrderId", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = fltOrdersTmp.OrderId});
                parameters.Add(new StatementParameter{ Name = "@PassengerName", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = fltOrdersTmp.PassengerName});
                parameters.Add(new StatementParameter{ Name = "@Sequence", Direction = ParameterDirection.Input, DbType = DbType.Int16, Value = fltOrdersTmp.Sequence});
                parameters.Add(new StatementParameter{ Name = "@AccCheckId", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = fltOrdersTmp.AccCheckId});
                parameters.Add(new StatementParameter{ Name = "@Price", Direction = ParameterDirection.Input, DbType = DbType.Currency, Value = fltOrdersTmp.Price});
                parameters.Add(new StatementParameter{ Name = "@Tax", Direction = ParameterDirection.Input, DbType = DbType.Currency, Value = fltOrdersTmp.Tax});
                parameters.Add(new StatementParameter{ Name = "@OilFee", Direction = ParameterDirection.Input, DbType = DbType.Currency, Value = fltOrdersTmp.OilFee});
                parameters.Add(new StatementParameter{ Name = "@Sendticketfee", Direction = ParameterDirection.Input, DbType = DbType.Currency, Value = fltOrdersTmp.Sendticketfee});
                parameters.Add(new StatementParameter{ Name = "@Insurancefee", Direction = ParameterDirection.Input, DbType = DbType.Currency, Value = fltOrdersTmp.Insurancefee});
                parameters.Add(new StatementParameter{ Name = "@ServiceFee", Direction = ParameterDirection.Input, DbType = DbType.Currency, Value = fltOrdersTmp.ServiceFee});
                parameters.Add(new StatementParameter{ Name = "@Refund", Direction = ParameterDirection.Input, DbType = DbType.Currency, Value = fltOrdersTmp.Refund});
                parameters.Add(new StatementParameter{ Name = "@delAdjustAmount", Direction = ParameterDirection.Input, DbType = DbType.Currency, Value = fltOrdersTmp.DelAdjustAmount});
                parameters.Add(new StatementParameter{ Name = "@AdjustedAmount", Direction = ParameterDirection.Input, DbType = DbType.Currency, Value = fltOrdersTmp.AdjustedAmount});
                parameters.Add(new StatementParameter{ Name = "@OrderStatus", Direction = ParameterDirection.Input, DbType = DbType.AnsiStringFixedLength, Value = fltOrdersTmp.OrderStatus});
                parameters.Add(new StatementParameter{ Name = "@Remark", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = fltOrdersTmp.Remark});
                parameters.Add(new StatementParameter{ Name = "@CreateTime", Direction = ParameterDirection.Input, DbType = DbType.DateTime, Value = fltOrdersTmp.CreateTime});
                parameters.Add(new StatementParameter{ Name = "@ConfirmTime", Direction = ParameterDirection.Input, DbType = DbType.DateTime, Value = fltOrdersTmp.ConfirmTime});
                parameters.Add(new StatementParameter{ Name = "@DailyConfirmFlag", Direction = ParameterDirection.Input, DbType = DbType.AnsiStringFixedLength, Value = fltOrdersTmp.DailyConfirmFlag});
                parameters.Add(new StatementParameter{ Name = "@DealID", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = fltOrdersTmp.DealID});
                parameters.Add(new StatementParameter{ Name = "@Cost", Direction = ParameterDirection.Input, DbType = DbType.Currency, Value = fltOrdersTmp.Cost});
                parameters.Add(new StatementParameter{ Name = "@DataChange_LastTime", Direction = ParameterDirection.Input, DbType = DbType.DateTime, Value = fltOrdersTmp.DataChange_LastTime});
                parameters.Add(new StatementParameter{ Name = "@return",  Direction = ParameterDirection.ReturnValue});

                baseDao.ExecSp("spA__FltOrdersTmp_i", parameters);

               fltOrdersTmp.RecordId = (int)parameters["@RecordId"].Value;
                return (int)parameters["@return"].Value;
            }
            catch (Exception ex)
            {
                throw new DalException("调用FltOrdersTmpDao时，访问Insert时出错", ex);
            }
        }
        
        /// <summary>
        /// 修改FltOrdersTmp
        /// </summary>
        /// <param name="fltOrdersTmp">FltOrdersTmp实体对象</param>
        /// <returns>状态代码</returns>
        public int UpdateFltOrdersTmp(FltOrdersTmp fltOrdersTmp)
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter{ Name = "@RecordId", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = fltOrdersTmp.RecordId});
                parameters.Add(new StatementParameter{ Name = "@OrderId", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = fltOrdersTmp.OrderId});
                parameters.Add(new StatementParameter{ Name = "@PassengerName", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = fltOrdersTmp.PassengerName});
                parameters.Add(new StatementParameter{ Name = "@Sequence", Direction = ParameterDirection.Input, DbType = DbType.Int16, Value = fltOrdersTmp.Sequence});
                parameters.Add(new StatementParameter{ Name = "@AccCheckId", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = fltOrdersTmp.AccCheckId});
                parameters.Add(new StatementParameter{ Name = "@Price", Direction = ParameterDirection.Input, DbType = DbType.Currency, Value = fltOrdersTmp.Price});
                parameters.Add(new StatementParameter{ Name = "@Tax", Direction = ParameterDirection.Input, DbType = DbType.Currency, Value = fltOrdersTmp.Tax});
                parameters.Add(new StatementParameter{ Name = "@OilFee", Direction = ParameterDirection.Input, DbType = DbType.Currency, Value = fltOrdersTmp.OilFee});
                parameters.Add(new StatementParameter{ Name = "@Sendticketfee", Direction = ParameterDirection.Input, DbType = DbType.Currency, Value = fltOrdersTmp.Sendticketfee});
                parameters.Add(new StatementParameter{ Name = "@Insurancefee", Direction = ParameterDirection.Input, DbType = DbType.Currency, Value = fltOrdersTmp.Insurancefee});
                parameters.Add(new StatementParameter{ Name = "@ServiceFee", Direction = ParameterDirection.Input, DbType = DbType.Currency, Value = fltOrdersTmp.ServiceFee});
                parameters.Add(new StatementParameter{ Name = "@Refund", Direction = ParameterDirection.Input, DbType = DbType.Currency, Value = fltOrdersTmp.Refund});
                parameters.Add(new StatementParameter{ Name = "@delAdjustAmount", Direction = ParameterDirection.Input, DbType = DbType.Currency, Value = fltOrdersTmp.DelAdjustAmount});
                parameters.Add(new StatementParameter{ Name = "@AdjustedAmount", Direction = ParameterDirection.Input, DbType = DbType.Currency, Value = fltOrdersTmp.AdjustedAmount});
                parameters.Add(new StatementParameter{ Name = "@OrderStatus", Direction = ParameterDirection.Input, DbType = DbType.AnsiStringFixedLength, Value = fltOrdersTmp.OrderStatus});
                parameters.Add(new StatementParameter{ Name = "@Remark", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = fltOrdersTmp.Remark});
                parameters.Add(new StatementParameter{ Name = "@CreateTime", Direction = ParameterDirection.Input, DbType = DbType.DateTime, Value = fltOrdersTmp.CreateTime});
                parameters.Add(new StatementParameter{ Name = "@ConfirmTime", Direction = ParameterDirection.Input, DbType = DbType.DateTime, Value = fltOrdersTmp.ConfirmTime});
                parameters.Add(new StatementParameter{ Name = "@DailyConfirmFlag", Direction = ParameterDirection.Input, DbType = DbType.AnsiStringFixedLength, Value = fltOrdersTmp.DailyConfirmFlag});
                parameters.Add(new StatementParameter{ Name = "@DealID", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = fltOrdersTmp.DealID});
                parameters.Add(new StatementParameter{ Name = "@Cost", Direction = ParameterDirection.Input, DbType = DbType.Currency, Value = fltOrdersTmp.Cost});
                parameters.Add(new StatementParameter{ Name = "@DataChange_LastTime", Direction = ParameterDirection.Input, DbType = DbType.DateTime, Value = fltOrdersTmp.DataChange_LastTime});
                parameters.Add(new StatementParameter{ Name = "@return",  Direction = ParameterDirection.ReturnValue});

                baseDao.ExecSp("spA__FltOrdersTmp_u", parameters);

                return (int)parameters["@return"].Value;
            }
            catch (Exception ex)
            {
                throw new DalException("调用FltOrdersTmpDao时，访问Update时出错", ex);
            }
        }
        
        /// <summary>
        /// 删除FltOrdersTmp
        /// </summary>
        /// <param name="fltOrdersTmp">FltOrdersTmp实体对象</param>
        /// <returns>状态代码</returns>
        public int DeleteFltOrdersTmp(FltOrdersTmp fltOrdersTmp)
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter{ Name = "@RecordId", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = fltOrdersTmp.RecordId});
                parameters.Add(new StatementParameter{ Name = "@return",  Direction = ParameterDirection.ReturnValue});

                baseDao.ExecSp("spA__FltOrdersTmp_d", parameters);

                return (int)parameters["@return"].Value;
            }
            catch (Exception ex)
            {
                throw new DalException("调用FltOrdersTmpDao时，访问Delete时出错", ex);
            }
        }
        
        /// <summary>
        /// 删除FltOrdersTmp
        /// </summary>
        /// <param name="recordId">@RecordId #></param>
        /// <returns>状态代码</returns>
        public int DeleteFltOrdersTmp(int recordId)
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter{ Name = "@RecordId", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = recordId});
                parameters.Add(new StatementParameter{ Name = "@return",  Direction = ParameterDirection.ReturnValue});

                baseDao.ExecSp("spA__FltOrdersTmp_d", parameters);

                return (int)parameters["@return"].Value;
            }
            catch (Exception ex)
            {
                throw new DalException("调用FltOrdersTmpDao时，访问DeleteFltOrdersTmp时出错", ex);
            }
        }
        

        /// <summary>
        /// 根据主键获取FltOrdersTmp信息
        /// </summary>
        /// <param name="recordId"></param>
        /// <returns>FltOrdersTmp信息</returns>
        public FltOrdersTmp FindByPk(int recordId )
        {
            try
            {
                return baseDao.GetByKey<FltOrdersTmp>(recordId);
            }
            catch (Exception ex)
            {
                throw new DalException("调用FltOrdersTmpDao时，访问FindByPk时出错", ex);
            }
        }

        /// <summary>
        /// 获取所有FltOrdersTmp信息
        /// </summary>
        /// <returns>FltOrdersTmp列表</returns>
        public IList<FltOrdersTmp> GetAll()
        {
            try
            {
                return baseDao.GetAll<FltOrdersTmp>();
            }
            catch (Exception ex)
            {
                throw new DalException("调用FltOrdersTmpDao时，访问GetAll时出错", ex);
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
                String sql = "SELECT count(1) from _FltOrdersTmp  with (nolock)  ";

                object obj = baseDao.ExecScalar(sql);
                long ret = Convert.ToInt64(obj);
                return ret;
            }
            catch (Exception ex)
            {
                throw new DalException("调用FltOrdersTmpDao时，访问Count时出错", ex);
            }
        }
        
        /// <summary>
        ///  检索FltOrdersTmp，带翻页
        /// </summary>
        /// <param name="obj">FltOrdersTmp实体对象检索条件</param>
        /// <param name="pagesize">每页记录数</param>
        /// <param name="pageNo">页码</param>
        /// <returns>检索结果</returns>
        public IList<FltOrdersTmp> GetListByPage(FltOrdersTmp obj, int pagesize, int pageNo)
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
                sbSql.Append(@"RecordId, OrderId, PassengerName, Sequence, AccCheckId, Price, Tax, OilFee, Sendticketfee, Insurancefee, ServiceFee, Refund, delAdjustAmount, AdjustedAmount, OrderStatus, Remark, CreateTime, ConfirmTime, DailyConfirmFlag, DealID, Cost, DataChangeLastTime from _FltOrdersTmp (nolock) ");

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
                sbSql.Append(@"select RecordId, OrderId, PassengerName, Sequence, AccCheckId, Price, Tax, OilFee, Sendticketfee, Insurancefee, ServiceFee, Refund, delAdjustAmount, AdjustedAmount, OrderStatus, Remark, CreateTime, ConfirmTime, DailyConfirmFlag, DealID, Cost, DataChangeLastTime from CTE Where rownum between @from and @end");
                dic.AddInParameter("@from", DbType.Int32, fromRownum);
                dic.AddInParameter("@end", DbType.Int32, endRownum);
                IList<FltOrdersTmp> list = baseDao.SelectList<FltOrdersTmp>(sbSql.ToString(), dic);

                return list;
            }
            catch (Exception ex)
            {
                throw new DalException("调用FltOrdersTmpDao时，访问GetListByPage时出错", ex);
            }
        }






        
    }
}
