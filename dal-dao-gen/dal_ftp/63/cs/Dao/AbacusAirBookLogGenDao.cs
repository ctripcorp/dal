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
    public partial class AbacusAirBookLogGenDao : IAbacusAirBookLogGenDao
    {
        readonly BaseDao baseDao = BaseDaoFactory.CreateBaseDao("AbacusDB_INSERT_1");
        
        /// <summary>
        ///  插入AbacusAirBookLogGen
        /// </summary>
        /// <param name="abacusAirBookLogGen">AbacusAirBookLogGen实体对象</param>
        /// <returns>状态代码</returns>
        public int InsertAbacusAirBookLogGen(AbacusAirBookLogGen abacusAirBookLogGen)
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter{ Name = "@LogID", Direction = ParameterDirection.InputOutput, DbType = DbType.Int32, Value = abacusAirBookLogGen.LogID});
                parameters.Add(new StatementParameter{ Name = "@ReferenceID", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = abacusAirBookLogGen.ReferenceID});
                parameters.Add(new StatementParameter{ Name = "@UID", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = abacusAirBookLogGen.UID});
                parameters.Add(new StatementParameter{ Name = "@OrderID", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = abacusAirBookLogGen.OrderID});
                parameters.Add(new StatementParameter{ Name = "@BeginTime", Direction = ParameterDirection.Input, DbType = DbType.DateTime, Value = abacusAirBookLogGen.BeginTime});
                parameters.Add(new StatementParameter{ Name = "@EndTime", Direction = ParameterDirection.Input, DbType = DbType.DateTime, Value = abacusAirBookLogGen.EndTime});
                parameters.Add(new StatementParameter{ Name = "@Result", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = abacusAirBookLogGen.Result});
                parameters.Add(new StatementParameter{ Name = "@PNR", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = abacusAirBookLogGen.PNR});
                parameters.Add(new StatementParameter{ Name = "@ErrDesc", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = abacusAirBookLogGen.ErrDesc});
                parameters.Add(new StatementParameter{ Name = "@Source", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = abacusAirBookLogGen.Source});
                parameters.Add(new StatementParameter{ Name = "@InsertTime", Direction = ParameterDirection.Input, DbType = DbType.DateTime, Value = abacusAirBookLogGen.InsertTime});
                parameters.Add(new StatementParameter{ Name = "@SnNo", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = abacusAirBookLogGen.SnNo});
                parameters.Add(new StatementParameter{ Name = "@DataChange_LastTime", Direction = ParameterDirection.Input, DbType = DbType.DateTime, Value = abacusAirBookLogGen.DataChange_LastTime});
                parameters.Add(new StatementParameter{ Name = "@return",  Direction = ParameterDirection.ReturnValue});

                baseDao.ExecSp("spA_AbacusAirBookLog_i", parameters);

               abacusAirBookLogGen.LogID = (int)parameters["@LogID"].Value;
                return (int)parameters["@return"].Value;
            }
            catch (Exception ex)
            {
                throw new DalException("调用AbacusAirBookLogGenDao时，访问Insert时出错", ex);
            }
        }
        
        /// <summary>
        /// 修改AbacusAirBookLogGen
        /// </summary>
        /// <param name="abacusAirBookLogGen">AbacusAirBookLogGen实体对象</param>
        /// <returns>状态代码</returns>
        public int UpdateAbacusAirBookLogGen(AbacusAirBookLogGen abacusAirBookLogGen)
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter{ Name = "@LogID", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = abacusAirBookLogGen.LogID});
                parameters.Add(new StatementParameter{ Name = "@ReferenceID", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = abacusAirBookLogGen.ReferenceID});
                parameters.Add(new StatementParameter{ Name = "@UID", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = abacusAirBookLogGen.UID});
                parameters.Add(new StatementParameter{ Name = "@OrderID", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = abacusAirBookLogGen.OrderID});
                parameters.Add(new StatementParameter{ Name = "@BeginTime", Direction = ParameterDirection.Input, DbType = DbType.DateTime, Value = abacusAirBookLogGen.BeginTime});
                parameters.Add(new StatementParameter{ Name = "@EndTime", Direction = ParameterDirection.Input, DbType = DbType.DateTime, Value = abacusAirBookLogGen.EndTime});
                parameters.Add(new StatementParameter{ Name = "@Result", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = abacusAirBookLogGen.Result});
                parameters.Add(new StatementParameter{ Name = "@PNR", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = abacusAirBookLogGen.PNR});
                parameters.Add(new StatementParameter{ Name = "@ErrDesc", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = abacusAirBookLogGen.ErrDesc});
                parameters.Add(new StatementParameter{ Name = "@Source", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = abacusAirBookLogGen.Source});
                parameters.Add(new StatementParameter{ Name = "@InsertTime", Direction = ParameterDirection.Input, DbType = DbType.DateTime, Value = abacusAirBookLogGen.InsertTime});
                parameters.Add(new StatementParameter{ Name = "@SnNo", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = abacusAirBookLogGen.SnNo});
                parameters.Add(new StatementParameter{ Name = "@DataChange_LastTime", Direction = ParameterDirection.Input, DbType = DbType.DateTime, Value = abacusAirBookLogGen.DataChange_LastTime});
                parameters.Add(new StatementParameter{ Name = "@return",  Direction = ParameterDirection.ReturnValue});

                baseDao.ExecSp("spA_AbacusAirBookLog_u", parameters);

                return (int)parameters["@return"].Value;
            }
            catch (Exception ex)
            {
                throw new DalException("调用AbacusAirBookLogGenDao时，访问Update时出错", ex);
            }
        }
        
        /// <summary>
        /// 删除AbacusAirBookLogGen
        /// </summary>
        /// <param name="abacusAirBookLogGen">AbacusAirBookLogGen实体对象</param>
        /// <returns>状态代码</returns>
        public int DeleteAbacusAirBookLogGen(AbacusAirBookLogGen abacusAirBookLogGen)
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter{ Name = "@LogID", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = abacusAirBookLogGen.LogID});
                parameters.Add(new StatementParameter{ Name = "@return",  Direction = ParameterDirection.ReturnValue});

                baseDao.ExecSp("spA_AbacusAirBookLog_d", parameters);

                return (int)parameters["@return"].Value;
            }
            catch (Exception ex)
            {
                throw new DalException("调用AbacusAirBookLogGenDao时，访问Delete时出错", ex);
            }
        }
        
        /// <summary>
        /// 删除AbacusAirBookLogGen
        /// </summary>
        /// <param name="logID">@LogID #></param>
        /// <returns>状态代码</returns>
        public int DeleteAbacusAirBookLogGen(int logID)
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter{ Name = "@LogID", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = logID});
                parameters.Add(new StatementParameter{ Name = "@return",  Direction = ParameterDirection.ReturnValue});

                baseDao.ExecSp("spA_AbacusAirBookLog_d", parameters);

                return (int)parameters["@return"].Value;
            }
            catch (Exception ex)
            {
                throw new DalException("调用AbacusAirBookLogGenDao时，访问DeleteAbacusAirBookLogGen时出错", ex);
            }
        }
        

        /// <summary>
        /// 根据主键获取AbacusAirBookLogGen信息
        /// </summary>
        /// <param name="logID"></param>
        /// <returns>AbacusAirBookLogGen信息</returns>
        public AbacusAirBookLogGen FindByPk(int logID )
        {
            try
            {
                return baseDao.GetByKey<AbacusAirBookLogGen>(logID);
            }
            catch (Exception ex)
            {
                throw new DalException("调用AbacusAirBookLogGenDao时，访问FindByPk时出错", ex);
            }
        }

        /// <summary>
        /// 获取所有AbacusAirBookLogGen信息
        /// </summary>
        /// <returns>AbacusAirBookLogGen列表</returns>
        public IList<AbacusAirBookLogGen> GetAll()
        {
            try
            {
                return baseDao.GetAll<AbacusAirBookLogGen>();
            }
            catch (Exception ex)
            {
                throw new DalException("调用AbacusAirBookLogGenDao时，访问GetAll时出错", ex);
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
                String sql = "SELECT count(1) from AbacusAirBookLog  with (nolock)  ";

                object obj = baseDao.ExecScalar(sql);
                long ret = Convert.ToInt64(obj);
                return ret;
            }
            catch (Exception ex)
            {
                throw new DalException("调用AbacusAirBookLogGenDao时，访问Count时出错", ex);
            }
        }
        
        /// <summary>
        ///  检索AbacusAirBookLogGen，带翻页
        /// </summary>
        /// <param name="obj">AbacusAirBookLogGen实体对象检索条件</param>
        /// <param name="pagesize">每页记录数</param>
        /// <param name="pageNo">页码</param>
        /// <returns>检索结果</returns>
        public IList<AbacusAirBookLogGen> GetListByPage(AbacusAirBookLogGen obj, int pagesize, int pageNo)
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
                sbSql.Append(@"LogID, ReferenceID, UID, OrderID, BeginTime, EndTime, Result, PNR, ErrDesc, Source, InsertTime, SnNo, DataChangeLastTime from AbacusAirBookLog (nolock) ");

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
                sbSql.Append(@"select LogID, ReferenceID, UID, OrderID, BeginTime, EndTime, Result, PNR, ErrDesc, Source, InsertTime, SnNo, DataChangeLastTime from CTE Where rownum between @from and @end");
                dic.AddInParameter("@from", DbType.Int32, fromRownum);
                dic.AddInParameter("@end", DbType.Int32, endRownum);
                IList<AbacusAirBookLogGen> list = baseDao.SelectList<AbacusAirBookLogGen>(sbSql.ToString(), dic);

                return list;
            }
            catch (Exception ex)
            {
                throw new DalException("调用AbacusAirBookLogGenDao时，访问GetListByPage时出错", ex);
            }
        }






        
    }
}
