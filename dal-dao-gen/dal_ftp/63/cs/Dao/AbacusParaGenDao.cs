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
    public partial class AbacusParaGenDao : IAbacusParaGenDao
    {
        readonly BaseDao baseDao = BaseDaoFactory.CreateBaseDao("AbacusDB_INSERT_1");
        
        /// <summary>
        ///  插入AbacusParaGen
        /// </summary>
        /// <param name="abacusParaGen">AbacusParaGen实体对象</param>
        /// <returns>状态代码</returns>
        public int InsertAbacusParaGen(AbacusParaGen abacusParaGen)
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter{ Name = "@ParaID", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = abacusParaGen.ParaID});
                parameters.Add(new StatementParameter{ Name = "@ParaTypeID", Direction = ParameterDirection.Input, DbType = DbType.Int16, Value = abacusParaGen.ParaTypeID});
                parameters.Add(new StatementParameter{ Name = "@ParaName", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = abacusParaGen.ParaName});
                parameters.Add(new StatementParameter{ Name = "@ParaValue", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = abacusParaGen.ParaValue});
                parameters.Add(new StatementParameter{ Name = "@Description", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = abacusParaGen.Description});
                parameters.Add(new StatementParameter{ Name = "@AbacusWSID", Direction = ParameterDirection.Input, DbType = DbType.Int16, Value = abacusParaGen.AbacusWSID});
                parameters.Add(new StatementParameter{ Name = "@DataChange_LastTime", Direction = ParameterDirection.Input, DbType = DbType.DateTime, Value = abacusParaGen.DataChange_LastTime});
                parameters.Add(new StatementParameter{ Name = "@return",  Direction = ParameterDirection.ReturnValue});

                baseDao.ExecSp("spA_AbacusPara_i", parameters);

                return (int)parameters["@return"].Value;
            }
            catch (Exception ex)
            {
                throw new DalException("调用AbacusParaGenDao时，访问Insert时出错", ex);
            }
        }
        
        /// <summary>
        /// 修改AbacusParaGen
        /// </summary>
        /// <param name="abacusParaGen">AbacusParaGen实体对象</param>
        /// <returns>状态代码</returns>
        public int UpdateAbacusParaGen(AbacusParaGen abacusParaGen)
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter{ Name = "@ParaID", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = abacusParaGen.ParaID});
                parameters.Add(new StatementParameter{ Name = "@ParaTypeID", Direction = ParameterDirection.Input, DbType = DbType.Int16, Value = abacusParaGen.ParaTypeID});
                parameters.Add(new StatementParameter{ Name = "@ParaName", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = abacusParaGen.ParaName});
                parameters.Add(new StatementParameter{ Name = "@ParaValue", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = abacusParaGen.ParaValue});
                parameters.Add(new StatementParameter{ Name = "@Description", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = abacusParaGen.Description});
                parameters.Add(new StatementParameter{ Name = "@AbacusWSID", Direction = ParameterDirection.Input, DbType = DbType.Int16, Value = abacusParaGen.AbacusWSID});
                parameters.Add(new StatementParameter{ Name = "@DataChange_LastTime", Direction = ParameterDirection.Input, DbType = DbType.DateTime, Value = abacusParaGen.DataChange_LastTime});
                parameters.Add(new StatementParameter{ Name = "@return",  Direction = ParameterDirection.ReturnValue});

                baseDao.ExecSp("spA_AbacusPara_u", parameters);

                return (int)parameters["@return"].Value;
            }
            catch (Exception ex)
            {
                throw new DalException("调用AbacusParaGenDao时，访问Update时出错", ex);
            }
        }
        
        /// <summary>
        /// 删除AbacusParaGen
        /// </summary>
        /// <param name="abacusParaGen">AbacusParaGen实体对象</param>
        /// <returns>状态代码</returns>
        public int DeleteAbacusParaGen(AbacusParaGen abacusParaGen)
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter{ Name = "@ParaID", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = abacusParaGen.ParaID});
                parameters.Add(new StatementParameter{ Name = "@return",  Direction = ParameterDirection.ReturnValue});

                baseDao.ExecSp("spA_AbacusPara_d", parameters);

                return (int)parameters["@return"].Value;
            }
            catch (Exception ex)
            {
                throw new DalException("调用AbacusParaGenDao时，访问Delete时出错", ex);
            }
        }
        
        /// <summary>
        /// 删除AbacusParaGen
        /// </summary>
        /// <param name="paraID">@ParaID #></param>
        /// <returns>状态代码</returns>
        public int DeleteAbacusParaGen(int paraID)
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter{ Name = "@ParaID", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = paraID});
                parameters.Add(new StatementParameter{ Name = "@return",  Direction = ParameterDirection.ReturnValue});

                baseDao.ExecSp("spA_AbacusPara_d", parameters);

                return (int)parameters["@return"].Value;
            }
            catch (Exception ex)
            {
                throw new DalException("调用AbacusParaGenDao时，访问DeleteAbacusParaGen时出错", ex);
            }
        }
        

        /// <summary>
        /// 根据主键获取AbacusParaGen信息
        /// </summary>
        /// <param name="paraID"></param>
        /// <returns>AbacusParaGen信息</returns>
        public AbacusParaGen FindByPk(int paraID )
        {
            try
            {
                return baseDao.GetByKey<AbacusParaGen>(paraID);
            }
            catch (Exception ex)
            {
                throw new DalException("调用AbacusParaGenDao时，访问FindByPk时出错", ex);
            }
        }

        /// <summary>
        /// 获取所有AbacusParaGen信息
        /// </summary>
        /// <returns>AbacusParaGen列表</returns>
        public IList<AbacusParaGen> GetAll()
        {
            try
            {
                return baseDao.GetAll<AbacusParaGen>();
            }
            catch (Exception ex)
            {
                throw new DalException("调用AbacusParaGenDao时，访问GetAll时出错", ex);
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
                String sql = "SELECT count(1) from AbacusPara  with (nolock)  ";

                object obj = baseDao.ExecScalar(sql);
                long ret = Convert.ToInt64(obj);
                return ret;
            }
            catch (Exception ex)
            {
                throw new DalException("调用AbacusParaGenDao时，访问Count时出错", ex);
            }
        }
        
        /// <summary>
        ///  检索AbacusParaGen，带翻页
        /// </summary>
        /// <param name="obj">AbacusParaGen实体对象检索条件</param>
        /// <param name="pagesize">每页记录数</param>
        /// <param name="pageNo">页码</param>
        /// <returns>检索结果</returns>
        public IList<AbacusParaGen> GetListByPage(AbacusParaGen obj, int pagesize, int pageNo)
        {
             try
            {
                var dic = new StatementParameterCollection();
                StringBuilder sbSql = new StringBuilder(200);

                
                 //计算ROWNUM
                int fromRownum = (pageNo - 1) * pagesize + 1;
                int endRownum = pagesize * pageNo;
                 sbSql.Append("WITH CTE AS ("); //WITH CTE 开始
                sbSql.Append("select row_number() over(order by ParaID desc ) as rownum, ");
                sbSql.Append(@"ParaID, ParaTypeID, ParaName, ParaValue, Description, AbacusWSID, DataChangeLastTime from AbacusPara (nolock) ");

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
                sbSql.Append(@"select ParaID, ParaTypeID, ParaName, ParaValue, Description, AbacusWSID, DataChangeLastTime from CTE Where rownum between @from and @end");
                dic.AddInParameter("@from", DbType.Int32, fromRownum);
                dic.AddInParameter("@end", DbType.Int32, endRownum);
                IList<AbacusParaGen> list = baseDao.SelectList<AbacusParaGen>(sbSql.ToString(), dic);

                return list;
            }
            catch (Exception ex)
            {
                throw new DalException("调用AbacusParaGenDao时，访问GetListByPage时出错", ex);
            }
        }






        
    }
}
