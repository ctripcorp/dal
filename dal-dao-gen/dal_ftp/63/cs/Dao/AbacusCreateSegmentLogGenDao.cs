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
    public partial class AbacusCreateSegmentLogGenDao : IAbacusCreateSegmentLogGenDao
    {
        readonly BaseDao baseDao = BaseDaoFactory.CreateBaseDao("AbacusDB_INSERT_1");
        
        /// <summary>
        ///  插入AbacusCreateSegmentLogGen
        /// </summary>
        /// <param name="abacusCreateSegmentLogGen">AbacusCreateSegmentLogGen实体对象</param>
        /// <returns>状态代码</returns>
        public int InsertAbacusCreateSegmentLogGen(AbacusCreateSegmentLogGen abacusCreateSegmentLogGen)
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter{ Name = "@LogID", Direction = ParameterDirection.InputOutput, DbType = DbType.Int32, Value = abacusCreateSegmentLogGen.LogID});
                parameters.Add(new StatementParameter{ Name = "@UID", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = abacusCreateSegmentLogGen.UID});
                parameters.Add(new StatementParameter{ Name = "@OrderID", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = abacusCreateSegmentLogGen.OrderID});
                parameters.Add(new StatementParameter{ Name = "@BeginTime", Direction = ParameterDirection.Input, DbType = DbType.DateTime, Value = abacusCreateSegmentLogGen.BeginTime});
                parameters.Add(new StatementParameter{ Name = "@EndTime", Direction = ParameterDirection.Input, DbType = DbType.DateTime, Value = abacusCreateSegmentLogGen.EndTime});
                parameters.Add(new StatementParameter{ Name = "@Result", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = abacusCreateSegmentLogGen.Result});
                parameters.Add(new StatementParameter{ Name = "@SessionID", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = abacusCreateSegmentLogGen.SessionID});
                parameters.Add(new StatementParameter{ Name = "@FlightSegmentStatus", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = abacusCreateSegmentLogGen.FlightSegmentStatus});
                parameters.Add(new StatementParameter{ Name = "@ErrDesc", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = abacusCreateSegmentLogGen.ErrDesc});
                parameters.Add(new StatementParameter{ Name = "@Source", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = abacusCreateSegmentLogGen.Source});
                parameters.Add(new StatementParameter{ Name = "@InsertTime", Direction = ParameterDirection.Input, DbType = DbType.DateTime, Value = abacusCreateSegmentLogGen.InsertTime});
                parameters.Add(new StatementParameter{ Name = "@DataChange_LastTime", Direction = ParameterDirection.Input, DbType = DbType.DateTime, Value = abacusCreateSegmentLogGen.DataChange_LastTime});
                parameters.Add(new StatementParameter{ Name = "@return",  Direction = ParameterDirection.ReturnValue});

                baseDao.ExecSp("spA_AbacusCreateSegmentLog_i", parameters);

               abacusCreateSegmentLogGen.LogID = (int)parameters["@LogID"].Value;
                return (int)parameters["@return"].Value;
            }
            catch (Exception ex)
            {
                throw new DalException("调用AbacusCreateSegmentLogGenDao时，访问Insert时出错", ex);
            }
        }
        
        /// <summary>
        /// 修改AbacusCreateSegmentLogGen
        /// </summary>
        /// <param name="abacusCreateSegmentLogGen">AbacusCreateSegmentLogGen实体对象</param>
        /// <returns>状态代码</returns>
        public int UpdateAbacusCreateSegmentLogGen(AbacusCreateSegmentLogGen abacusCreateSegmentLogGen)
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter{ Name = "@LogID", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = abacusCreateSegmentLogGen.LogID});
                parameters.Add(new StatementParameter{ Name = "@UID", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = abacusCreateSegmentLogGen.UID});
                parameters.Add(new StatementParameter{ Name = "@OrderID", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = abacusCreateSegmentLogGen.OrderID});
                parameters.Add(new StatementParameter{ Name = "@BeginTime", Direction = ParameterDirection.Input, DbType = DbType.DateTime, Value = abacusCreateSegmentLogGen.BeginTime});
                parameters.Add(new StatementParameter{ Name = "@EndTime", Direction = ParameterDirection.Input, DbType = DbType.DateTime, Value = abacusCreateSegmentLogGen.EndTime});
                parameters.Add(new StatementParameter{ Name = "@Result", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = abacusCreateSegmentLogGen.Result});
                parameters.Add(new StatementParameter{ Name = "@SessionID", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = abacusCreateSegmentLogGen.SessionID});
                parameters.Add(new StatementParameter{ Name = "@FlightSegmentStatus", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = abacusCreateSegmentLogGen.FlightSegmentStatus});
                parameters.Add(new StatementParameter{ Name = "@ErrDesc", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = abacusCreateSegmentLogGen.ErrDesc});
                parameters.Add(new StatementParameter{ Name = "@Source", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = abacusCreateSegmentLogGen.Source});
                parameters.Add(new StatementParameter{ Name = "@InsertTime", Direction = ParameterDirection.Input, DbType = DbType.DateTime, Value = abacusCreateSegmentLogGen.InsertTime});
                parameters.Add(new StatementParameter{ Name = "@DataChange_LastTime", Direction = ParameterDirection.Input, DbType = DbType.DateTime, Value = abacusCreateSegmentLogGen.DataChange_LastTime});
                parameters.Add(new StatementParameter{ Name = "@return",  Direction = ParameterDirection.ReturnValue});

                baseDao.ExecSp("spA_AbacusCreateSegmentLog_u", parameters);

                return (int)parameters["@return"].Value;
            }
            catch (Exception ex)
            {
                throw new DalException("调用AbacusCreateSegmentLogGenDao时，访问Update时出错", ex);
            }
        }
        
        /// <summary>
        /// 删除AbacusCreateSegmentLogGen
        /// </summary>
        /// <param name="abacusCreateSegmentLogGen">AbacusCreateSegmentLogGen实体对象</param>
        /// <returns>状态代码</returns>
        public int DeleteAbacusCreateSegmentLogGen(AbacusCreateSegmentLogGen abacusCreateSegmentLogGen)
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter{ Name = "@LogID", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = abacusCreateSegmentLogGen.LogID});
                parameters.Add(new StatementParameter{ Name = "@return",  Direction = ParameterDirection.ReturnValue});

                baseDao.ExecSp("spA_AbacusCreateSegmentLog_d", parameters);

                return (int)parameters["@return"].Value;
            }
            catch (Exception ex)
            {
                throw new DalException("调用AbacusCreateSegmentLogGenDao时，访问Delete时出错", ex);
            }
        }
        
        /// <summary>
        /// 删除AbacusCreateSegmentLogGen
        /// </summary>
        /// <param name="logID">@LogID #></param>
        /// <returns>状态代码</returns>
        public int DeleteAbacusCreateSegmentLogGen(int logID)
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter{ Name = "@LogID", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = logID});
                parameters.Add(new StatementParameter{ Name = "@return",  Direction = ParameterDirection.ReturnValue});

                baseDao.ExecSp("spA_AbacusCreateSegmentLog_d", parameters);

                return (int)parameters["@return"].Value;
            }
            catch (Exception ex)
            {
                throw new DalException("调用AbacusCreateSegmentLogGenDao时，访问DeleteAbacusCreateSegmentLogGen时出错", ex);
            }
        }
        

        /// <summary>
        /// 根据主键获取AbacusCreateSegmentLogGen信息
        /// </summary>
        /// <param name="logID"></param>
        /// <returns>AbacusCreateSegmentLogGen信息</returns>
        public AbacusCreateSegmentLogGen FindByPk(int logID )
        {
            try
            {
                return baseDao.GetByKey<AbacusCreateSegmentLogGen>(logID);
            }
            catch (Exception ex)
            {
                throw new DalException("调用AbacusCreateSegmentLogGenDao时，访问FindByPk时出错", ex);
            }
        }

        /// <summary>
        /// 获取所有AbacusCreateSegmentLogGen信息
        /// </summary>
        /// <returns>AbacusCreateSegmentLogGen列表</returns>
        public IList<AbacusCreateSegmentLogGen> GetAll()
        {
            try
            {
                return baseDao.GetAll<AbacusCreateSegmentLogGen>();
            }
            catch (Exception ex)
            {
                throw new DalException("调用AbacusCreateSegmentLogGenDao时，访问GetAll时出错", ex);
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
                String sql = "SELECT count(1) from AbacusCreateSegmentLog  with (nolock)  ";

                object obj = baseDao.ExecScalar(sql);
                long ret = Convert.ToInt64(obj);
                return ret;
            }
            catch (Exception ex)
            {
                throw new DalException("调用AbacusCreateSegmentLogGenDao时，访问Count时出错", ex);
            }
        }
        
        /// <summary>
        ///  检索AbacusCreateSegmentLogGen，带翻页
        /// </summary>
        /// <param name="obj">AbacusCreateSegmentLogGen实体对象检索条件</param>
        /// <param name="pagesize">每页记录数</param>
        /// <param name="pageNo">页码</param>
        /// <returns>检索结果</returns>
        public IList<AbacusCreateSegmentLogGen> GetListByPage(AbacusCreateSegmentLogGen obj, int pagesize, int pageNo)
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
                sbSql.Append(@"LogID, UID, OrderID, BeginTime, EndTime, Result, SessionID, FlightSegmentStatus, ErrDesc, Source, InsertTime, DataChangeLastTime from AbacusCreateSegmentLog (nolock) ");

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
                sbSql.Append(@"select LogID, UID, OrderID, BeginTime, EndTime, Result, SessionID, FlightSegmentStatus, ErrDesc, Source, InsertTime, DataChangeLastTime from CTE Where rownum between @from and @end");
                dic.AddInParameter("@from", DbType.Int32, fromRownum);
                dic.AddInParameter("@end", DbType.Int32, endRownum);
                IList<AbacusCreateSegmentLogGen> list = baseDao.SelectList<AbacusCreateSegmentLogGen>(sbSql.ToString(), dic);

                return list;
            }
            catch (Exception ex)
            {
                throw new DalException("调用AbacusCreateSegmentLogGenDao时，访问GetListByPage时出错", ex);
            }
        }






        
    }
}
