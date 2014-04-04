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
    public partial class AbacusModifyInfoLogGenDao : IAbacusModifyInfoLogGenDao
    {
        readonly BaseDao baseDao = BaseDaoFactory.CreateBaseDao("AbacusDB_INSERT_1");
        
        /// <summary>
        ///  插入AbacusModifyInfoLogGen
        /// </summary>
        /// <param name="abacusModifyInfoLogGen">AbacusModifyInfoLogGen实体对象</param>
        /// <returns>状态代码</returns>
        public int InsertAbacusModifyInfoLogGen(AbacusModifyInfoLogGen abacusModifyInfoLogGen)
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter{ Name = "@LogID", Direction = ParameterDirection.InputOutput, DbType = DbType.Int32, Value = abacusModifyInfoLogGen.LogID});
                parameters.Add(new StatementParameter{ Name = "@ReferenceID", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = abacusModifyInfoLogGen.ReferenceID});
                parameters.Add(new StatementParameter{ Name = "@UID", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = abacusModifyInfoLogGen.UID});
                parameters.Add(new StatementParameter{ Name = "@OrderID", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = abacusModifyInfoLogGen.OrderID});
                parameters.Add(new StatementParameter{ Name = "@PNR", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = abacusModifyInfoLogGen.PNR});
                parameters.Add(new StatementParameter{ Name = "@BeginTime", Direction = ParameterDirection.Input, DbType = DbType.DateTime, Value = abacusModifyInfoLogGen.BeginTime});
                parameters.Add(new StatementParameter{ Name = "@EndTime", Direction = ParameterDirection.Input, DbType = DbType.DateTime, Value = abacusModifyInfoLogGen.EndTime});
                parameters.Add(new StatementParameter{ Name = "@Result", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = abacusModifyInfoLogGen.Result});
                parameters.Add(new StatementParameter{ Name = "@ErrDesc", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = abacusModifyInfoLogGen.ErrDesc});
                parameters.Add(new StatementParameter{ Name = "@Source", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = abacusModifyInfoLogGen.Source});
                parameters.Add(new StatementParameter{ Name = "@InsertTime", Direction = ParameterDirection.Input, DbType = DbType.DateTime, Value = abacusModifyInfoLogGen.InsertTime});
                parameters.Add(new StatementParameter{ Name = "@DataChange_LastTime", Direction = ParameterDirection.Input, DbType = DbType.DateTime, Value = abacusModifyInfoLogGen.DataChange_LastTime});
                parameters.Add(new StatementParameter{ Name = "@return",  Direction = ParameterDirection.ReturnValue});

                baseDao.ExecSp("spA_AbacusModifyInfoLog_i", parameters);

               abacusModifyInfoLogGen.LogID = (int)parameters["@LogID"].Value;
                return (int)parameters["@return"].Value;
            }
            catch (Exception ex)
            {
                throw new DalException("调用AbacusModifyInfoLogGenDao时，访问Insert时出错", ex);
            }
        }
        
        /// <summary>
        /// 修改AbacusModifyInfoLogGen
        /// </summary>
        /// <param name="abacusModifyInfoLogGen">AbacusModifyInfoLogGen实体对象</param>
        /// <returns>状态代码</returns>
        public int UpdateAbacusModifyInfoLogGen(AbacusModifyInfoLogGen abacusModifyInfoLogGen)
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter{ Name = "@LogID", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = abacusModifyInfoLogGen.LogID});
                parameters.Add(new StatementParameter{ Name = "@ReferenceID", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = abacusModifyInfoLogGen.ReferenceID});
                parameters.Add(new StatementParameter{ Name = "@UID", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = abacusModifyInfoLogGen.UID});
                parameters.Add(new StatementParameter{ Name = "@OrderID", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = abacusModifyInfoLogGen.OrderID});
                parameters.Add(new StatementParameter{ Name = "@PNR", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = abacusModifyInfoLogGen.PNR});
                parameters.Add(new StatementParameter{ Name = "@BeginTime", Direction = ParameterDirection.Input, DbType = DbType.DateTime, Value = abacusModifyInfoLogGen.BeginTime});
                parameters.Add(new StatementParameter{ Name = "@EndTime", Direction = ParameterDirection.Input, DbType = DbType.DateTime, Value = abacusModifyInfoLogGen.EndTime});
                parameters.Add(new StatementParameter{ Name = "@Result", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = abacusModifyInfoLogGen.Result});
                parameters.Add(new StatementParameter{ Name = "@ErrDesc", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = abacusModifyInfoLogGen.ErrDesc});
                parameters.Add(new StatementParameter{ Name = "@Source", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = abacusModifyInfoLogGen.Source});
                parameters.Add(new StatementParameter{ Name = "@InsertTime", Direction = ParameterDirection.Input, DbType = DbType.DateTime, Value = abacusModifyInfoLogGen.InsertTime});
                parameters.Add(new StatementParameter{ Name = "@DataChange_LastTime", Direction = ParameterDirection.Input, DbType = DbType.DateTime, Value = abacusModifyInfoLogGen.DataChange_LastTime});
                parameters.Add(new StatementParameter{ Name = "@return",  Direction = ParameterDirection.ReturnValue});

                baseDao.ExecSp("spA_AbacusModifyInfoLog_u", parameters);

                return (int)parameters["@return"].Value;
            }
            catch (Exception ex)
            {
                throw new DalException("调用AbacusModifyInfoLogGenDao时，访问Update时出错", ex);
            }
        }
        
        /// <summary>
        /// 删除AbacusModifyInfoLogGen
        /// </summary>
        /// <param name="abacusModifyInfoLogGen">AbacusModifyInfoLogGen实体对象</param>
        /// <returns>状态代码</returns>
        public int DeleteAbacusModifyInfoLogGen(AbacusModifyInfoLogGen abacusModifyInfoLogGen)
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter{ Name = "@LogID", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = abacusModifyInfoLogGen.LogID});
                parameters.Add(new StatementParameter{ Name = "@return",  Direction = ParameterDirection.ReturnValue});

                baseDao.ExecSp("spA_AbacusModifyInfoLog_d", parameters);

                return (int)parameters["@return"].Value;
            }
            catch (Exception ex)
            {
                throw new DalException("调用AbacusModifyInfoLogGenDao时，访问Delete时出错", ex);
            }
        }
        
        /// <summary>
        /// 删除AbacusModifyInfoLogGen
        /// </summary>
        /// <param name="logID">@LogID #></param>
        /// <returns>状态代码</returns>
        public int DeleteAbacusModifyInfoLogGen(int logID)
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter{ Name = "@LogID", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = logID});
                parameters.Add(new StatementParameter{ Name = "@return",  Direction = ParameterDirection.ReturnValue});

                baseDao.ExecSp("spA_AbacusModifyInfoLog_d", parameters);

                return (int)parameters["@return"].Value;
            }
            catch (Exception ex)
            {
                throw new DalException("调用AbacusModifyInfoLogGenDao时，访问DeleteAbacusModifyInfoLogGen时出错", ex);
            }
        }
        

        /// <summary>
        /// 根据主键获取AbacusModifyInfoLogGen信息
        /// </summary>
        /// <param name="logID"></param>
        /// <returns>AbacusModifyInfoLogGen信息</returns>
        public AbacusModifyInfoLogGen FindByPk(int logID )
        {
            try
            {
                return baseDao.GetByKey<AbacusModifyInfoLogGen>(logID);
            }
            catch (Exception ex)
            {
                throw new DalException("调用AbacusModifyInfoLogGenDao时，访问FindByPk时出错", ex);
            }
        }

        /// <summary>
        /// 获取所有AbacusModifyInfoLogGen信息
        /// </summary>
        /// <returns>AbacusModifyInfoLogGen列表</returns>
        public IList<AbacusModifyInfoLogGen> GetAll()
        {
            try
            {
                return baseDao.GetAll<AbacusModifyInfoLogGen>();
            }
            catch (Exception ex)
            {
                throw new DalException("调用AbacusModifyInfoLogGenDao时，访问GetAll时出错", ex);
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
                String sql = "SELECT count(1) from AbacusModifyInfoLog  with (nolock)  ";

                object obj = baseDao.ExecScalar(sql);
                long ret = Convert.ToInt64(obj);
                return ret;
            }
            catch (Exception ex)
            {
                throw new DalException("调用AbacusModifyInfoLogGenDao时，访问Count时出错", ex);
            }
        }
        
        /// <summary>
        ///  检索AbacusModifyInfoLogGen，带翻页
        /// </summary>
        /// <param name="obj">AbacusModifyInfoLogGen实体对象检索条件</param>
        /// <param name="pagesize">每页记录数</param>
        /// <param name="pageNo">页码</param>
        /// <returns>检索结果</returns>
        public IList<AbacusModifyInfoLogGen> GetListByPage(AbacusModifyInfoLogGen obj, int pagesize, int pageNo)
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
                sbSql.Append(@"LogID, ReferenceID, UID, OrderID, PNR, BeginTime, EndTime, Result, ErrDesc, Source, InsertTime, DataChangeLastTime from AbacusModifyInfoLog (nolock) ");

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
                sbSql.Append(@"select LogID, ReferenceID, UID, OrderID, PNR, BeginTime, EndTime, Result, ErrDesc, Source, InsertTime, DataChangeLastTime from CTE Where rownum between @from and @end");
                dic.AddInParameter("@from", DbType.Int32, fromRownum);
                dic.AddInParameter("@end", DbType.Int32, endRownum);
                IList<AbacusModifyInfoLogGen> list = baseDao.SelectList<AbacusModifyInfoLogGen>(sbSql.ToString(), dic);

                return list;
            }
            catch (Exception ex)
            {
                throw new DalException("调用AbacusModifyInfoLogGenDao时，访问GetListByPage时出错", ex);
            }
        }






        
    }
}
