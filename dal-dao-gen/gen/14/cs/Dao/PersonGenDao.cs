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
    public partial class PersonGenDao : IPersonGenDao
    {
        readonly BaseDao baseDao = BaseDaoFactory.CreateBaseDao("PerformanceTest");
        
        /// <summary>
        ///  插入PersonGen
        /// </summary>
        /// <param name="personGen">PersonGen实体对象</param>
        /// <returns>状态代码</returns>
        public int InsertPersonGen(PersonGen personGen)
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter{ Name = "@ID", Direction = ParameterDirection.InputOutput, DbType = DbType.Int32, Value = personGen.ID});
                parameters.Add(new StatementParameter{ Name = "@Name", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = personGen.Name});
                parameters.Add(new StatementParameter{ Name = "@Age", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = personGen.Age});
                parameters.Add(new StatementParameter{ Name = "@Birth", Direction = ParameterDirection.Input, DbType = DbType.DateTime, Value = personGen.Birth});
                parameters.Add(new StatementParameter{ Name = "@return",  Direction = ParameterDirection.ReturnValue});

                baseDao.ExecSp("spA_Person_i", parameters);

               personGen.ID = (int)parameters["@ID"].Value;
                return (int)parameters["@return"].Value;
            }
            catch (Exception ex)
            {
                throw new DalException("调用PersonGenDao时，访问Insert时出错", ex);
            }
        }
        
        /// <summary>
        /// 修改PersonGen
        /// </summary>
        /// <param name="personGen">PersonGen实体对象</param>
        /// <returns>状态代码</returns>
        public int UpdatePersonGen(PersonGen personGen)
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter{ Name = "@ID", Direction = ParameterDirection.InputOutput, DbType = DbType.Int32, Value = personGen.ID});
                parameters.Add(new StatementParameter{ Name = "@Name", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = personGen.Name});
                parameters.Add(new StatementParameter{ Name = "@Age", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = personGen.Age});
                parameters.Add(new StatementParameter{ Name = "@Birth", Direction = ParameterDirection.Input, DbType = DbType.DateTime, Value = personGen.Birth});
                parameters.Add(new StatementParameter{ Name = "@return",  Direction = ParameterDirection.ReturnValue});

                baseDao.ExecSp("spA_Person_u", parameters);

               personGen.ID = (int)parameters["@ID"].Value;
                return (int)parameters["@return"].Value;
            }
            catch (Exception ex)
            {
                throw new DalException("调用PersonGenDao时，访问Update时出错", ex);
            }
        }
        
        /// <summary>
        /// 删除PersonGen
        /// </summary>
        /// <param name="personGen">PersonGen实体对象</param>
        /// <returns>状态代码</returns>
        public int DeletePersonGen(PersonGen personGen)
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter{ Name = "@ID", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = personGen.ID});
                parameters.Add(new StatementParameter{ Name = "@return",  Direction = ParameterDirection.ReturnValue});

                baseDao.ExecSp("spA_Person_d", parameters);

                return (int)parameters["@return"].Value;
            }
            catch (Exception ex)
            {
                throw new DalException("调用PersonGenDao时，访问Delete时出错", ex);
            }
        }
        
        /// <summary>
        /// 删除<#= className #>
        /// </summary>
        /// <param name="iD">@ID #></param>
        /// <returns>状态代码</returns>
        public int DeletePersonGen(int iD)
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter{ Name = "@ID", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = iD});
                parameters.Add(new StatementParameter{ Name = "@return",  Direction = ParameterDirection.ReturnValue});

                baseDao.ExecSp("spA_Person_d", parameters);

                return (int)parameters["@return"].Value;
            }
            catch (Exception ex)
            {
                throw new DalException("调用PersonGenDao时，访问DeletePersonGen时出错", ex);
            }
        }
        

        /// <summary>
        /// 根据主键获取PersonGen信息
        /// </summary>
        /// <param name="iD"></param>
        /// <returns>PersonGen信息</returns>
        public PersonGen FindByPk(int iD )
        {
            try
            {
                return baseDao.GetByKey<PersonGen>(iD);
            }
            catch (Exception ex)
            {
                throw new DalException("调用PersonGenDao时，访问FindByPk时出错", ex);
            }
        }

        /// <summary>
        /// 获取所有PersonGen信息
        /// </summary>
        /// <returns>PersonGen列表</returns>
        public IList<PersonGen> GetAll()
        {
            try
            {
                return baseDao.GetAll<PersonGen>();
            }
            catch (Exception ex)
            {
                throw new DalException("调用PersonGenDao时，访问GetAll时出错", ex);
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
                String sql = "SELECT count(1) from Person  with (nolock)  ";

                object obj = baseDao.ExecScalar(sql);
                long ret = Convert.ToInt64(obj);
                return ret;
            }
            catch (Exception ex)
            {
                throw new DalException("调用PersonGenDao时，访问Count时出错", ex);
            }
        }
        
        /// <summary>
        ///  检索PersonGen，带翻页
        /// </summary>
        /// <param name="obj">PersonGen实体对象检索条件</param>
        /// <param name="pagesize">每页记录数</param>
        /// <param name="pageNo">页码</param>
        /// <returns>检索结果</returns>
        public IList<PersonGen> GetListByPage(PersonGen obj, int pagesize, int pageNo)
        {
             try
            {
                var dic = new StatementParameterCollection();
                StringBuilder sbSql = new StringBuilder(200);

                
                 //计算ROWNUM
                int fromRownum = (pageNo - 1) * pagesize + 1;
                int endRownum = pagesize * pageNo;
                 sbSql.Append("WITH CTE AS ("); //WITH CTE 开始
                sbSql.Append("select row_number() over(order by ID desc ) as rownum, ");
                sbSql.Append(@"ID, Name, Age, Birth from Person (nolock) ");

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
                sbSql.Append(@"select ID, Name, Age, Birth from CTE Where rownum between @from and @end");
                dic.AddInParameter("@from", DbType.Int32, fromRownum);
                dic.AddInParameter("@end", DbType.Int32, endRownum);
                IList<PersonGen> list = baseDao.SelectList<PersonGen>(sbSql.ToString(), dic);

                return list;
            }
            catch (Exception ex)
            {
                throw new DalException("调用PersonGenDao时，访问GetListByPage时出错", ex);
            }
        }

        /// <summary>
        ///  转换List为DataTable
        /// </summary>
        /// <param name="personGenlist">PersonGen实体对象列表</param>
        /// <param name="isInsert">PersonGen实体对象列表</param>
        /// <returns>DataTable</returns>
        private DataTable ToDataTable(IList<PersonGen> personGenList , bool insert)
        {
            DataTable dt = new DataTable();
            dt.Columns.Add("ID", typeof(int));
            dt.Columns.Add("Name", typeof(string));
            dt.Columns.Add("Age", typeof(int));
            dt.Columns.Add("Birth", typeof(DateTime));

            int i = 0;
            foreach (PersonGen personGenInfo in personGenList)
            {
                DataRow row = dt.NewRow();
                row["ID"] = insert ? ++i : personGenInfo.ID;
                row["Name"] = personGenInfo.Name;
                row["Age"] = personGenInfo.Age;
                row["Birth"] = personGenInfo.Birth;
                dt.Rows.Add(row);
            }
            return dt;
        }


        /// <summary>
        ///  批量插入PersonGen
        /// </summary>
        /// <param name="personGen">PersonGen实体对象列表</param>
        /// <returns>状态代码</returns>
        public int BulkInsertPersonGen(IList<PersonGen> personGenList)
        {
            try
            {
                DataTable dt = this.ToDataTable(personGenList,true);

                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter { Value = dt, Name = "TVP_Person", ExtendTypeValue = SqlDbType.Structured, ExtendType = 1 });
                parameters.Add(new StatementParameter { Name = "@return", Direction = ParameterDirection.ReturnValue });

                baseDao.ExecSp("spT_Person_i", parameters);
                return (int)parameters["@return"].Value;
               
            }
            catch (Exception ex)
            {
                throw new DalException("调用PersonGenDao时，访问BulkInsert时出错", ex);
            }
        }

        /// <summary>
        ///  批量修改PersonGen
        /// </summary>
        /// <param name="personGen">PersonGen实体对象列表</param>
        /// <returns>状态代码</returns>
        public int BulkUpdatePersonGen(IList<PersonGen> personGenList)
        {
            try
            {
                DataTable dt = this.ToDataTable(personGenList,false);

                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter { Value = dt, Name = "TVP_Person", ExtendTypeValue = SqlDbType.Structured, ExtendType = 1 });
                parameters.Add(new StatementParameter { Name = "@return", Direction = ParameterDirection.ReturnValue });

                baseDao.ExecSp("spT_Person_u", parameters);
                return (int)parameters["@return"].Value;
               
            }
            catch (Exception ex)
            {
                throw new DalException("调用PersonGenDao时，访问BulkUpdate时出错", ex);
            }
        }

        /// <summary>
        ///  批量删除PersonGen
        /// </summary>
        /// <param name="personGen">PersonGen实体对象列表</param>
        /// <returns>状态代码</returns>
        public int BulkDeletePersonGen(IList<PersonGen> personGenList)
        {
            try
            {
                DataTable dt = this.ToDataTable(personGenList,false);

                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter { Value = dt, Name = "TVP_Person", ExtendTypeValue = SqlDbType.Structured, ExtendType = 1 });
                parameters.Add(new StatementParameter { Name = "@return", Direction = ParameterDirection.ReturnValue });

                baseDao.ExecSp("spT_Person_d", parameters);
                return (int)parameters["@return"].Value;
               
            }
            catch (Exception ex)
            {
                throw new DalException("调用PersonGenDao时，访问BulkDelete时出错", ex);
            }
        }

		/// <summary>
        ///  GetNameByID
        /// </summary>
        /// <param name="iD"></param>
        /// <returns></returns>
        public IList<PersonGen> GetNameByID(int iD)
        {
        	try
            {
            	string sql = "SELECT Name FROM Person WHERE  ID = @ID ";
                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter{ Name = "@ID", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value =iD });

                return baseDao.SelectList<PersonGen>(sql, parameters);

            }
            catch (Exception ex)
            {
                throw new DalException("调用PersonGenDao时，访问GetNameByID时出错", ex);
            }
        }
		/// <summary>
        ///  deleteByName
        /// </summary>
        /// <param name="name"></param>
        /// <returns></returns>
        public int deleteByName(string name)
        {
        	try
            {
            	string sql = "Delete FROM Person WHERE  Name = @Name ";
                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter{ Name = "@Name", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value =name });

				return baseDao.ExecNonQuery(sql, parameters);

            }
            catch (Exception ex)
            {
                throw new DalException("调用PersonGenDao时，访问deleteByName时出错", ex);
            }
        }
        
    }
}
