using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Text;
using Arch.Data;
using Arch.Data.DbEngine;
using com.ctrip.platform.tools.Entity.DataModel;
using com.ctrip.platform.tools.Interface.IDao;

namespace com.ctrip.platform.tools.Dao
{
   /// <summary>
    /// 更多DALFx接口功能，请参阅DALFx Confluence，地址：
    /// http://conf.ctripcorp.com/display/ARCH/Dal+Fx+API
    /// </summary>
    public partial class Person_genDao : IPerson_genDao
    {
        readonly BaseDao baseDao = BaseDaoFactory.CreateBaseDao("PerformanceTest");
        
        /// <summary>
        ///  插入Person_gen
        /// </summary>
        /// <param name="person_gen">Person_gen实体对象</param>
        /// <returns>新增的主键</returns>
        public int InsertPerson_gen(Person_gen person_gen)
        {
            try
            {
                Object result = baseDao.Insert<Person_gen>(person_gen);
                int iReturn = Convert.ToInt32(result);

                return iReturn;
            }
            catch (Exception ex)
            {
                throw new DalException("调用Person_gen时，访问Insert时出错", ex);
            }
        }
        
        /// <summary>
        /// 修改Person_gen
        /// </summary>
        /// <param name="person_gen">Person_gen实体对象</param>
        /// <returns>状态代码</returns>
        public int UpdatePerson_gen(Person_gen person_gen)
        {
            try
            {
                Object result = baseDao.Update<Person_gen>(person_gen);
                int iReturn = Convert.ToInt32(result);

                return iReturn;
            }
            catch (Exception ex)
            {
                throw new DalException("调用Person_gen时，访问Update时出错", ex);
            }
        }
        
        /// <summary>
        /// 删除Person_gen
        /// </summary>
        /// <param name="person_gen">Person_gen实体对象</param>
        /// <returns>状态代码</returns>
        public int DeletePerson_gen(Person_gen person_gen)
        {
            try
            {
                Object result = baseDao.Delete<Person_gen>(person_gen);
                int iReturn = Convert.ToInt32(result);

                return iReturn;
            }
            catch (Exception ex)
            {
                throw new DalException("调用Person_gen时，访问Delete时出错", ex);
            }
        }
        
        

        /// <summary>
        /// 根据主键获取Person_gen信息
        /// </summary>
        /// <param name="iD"></param>
        /// <returns>Person_gen信息</returns>
        public Person_gen FindByPk(uint iD )
        {
            try
            {
                return baseDao.GetByKey<Person_gen>(iD);
            }
            catch (Exception ex)
            {
                throw new DalException("调用Person_genDao时，访问FindByPk时出错", ex);
            }
        }

        /// <summary>
        /// 获取所有Person_gen信息
        /// </summary>
        /// <returns>Person_gen列表</returns>
        public IList<Person_gen> GetAll()
        {
            try
            {
                return baseDao.GetAll<Person_gen>();
            }
            catch (Exception ex)
            {
                throw new DalException("调用Person_genDao时，访问GetAll时出错", ex);
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
                throw new DalException("调用Person_genDao时，访问Count时出错", ex);
            }
        }
        
        /// <summary>
        ///  检索Person_gen，带翻页
        /// </summary>
        /// <param name="obj">Person_gen实体对象检索条件</param>
        /// <param name="pagesize">每页记录数</param>
        /// <param name="pageNo">页码</param>
        /// <returns>检索结果</returns>
        public IList<Person_gen> GetListByPage(Person_gen obj, int pagesize, int pageNo)
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
                IList<Person_gen> list = baseDao.SelectList<Person_gen>(sbSql.ToString(), dic);

                return list;
            }
            catch (Exception ex)
            {
                throw new DalException("调用Person_genDao时，访问GetListByPage时出错", ex);
            }
        }

        /// <summary>
        ///  转换List为DataTable
        /// </summary>
        /// <param name="person_genlist">Person_gen实体对象列表</param>
        /// <param name="isInsert">Person_gen实体对象列表</param>
        /// <returns>DataTable</returns>
        private DataTable ToDataTable(IList<Person_gen> person_genList , bool insert)
        {
            DataTable dt = new DataTable();
            dt.Columns.Add("ID", typeof(uint));
            dt.Columns.Add("Name", typeof(string));
            dt.Columns.Add("Age", typeof(uint));
            dt.Columns.Add("Birth", typeof(DateTime));

            int i = 0;
            foreach (Person_gen person_genInfo in person_genList)
            {
                DataRow row = dt.NewRow();
                row["ID"] = insert ? ++i : person_genInfo.ID;
                row["Name"] = person_genInfo.Name;
                row["Age"] = person_genInfo.Age;
                row["Birth"] = person_genInfo.Birth;
                dt.Rows.Add(row);
            }
            return dt;
        }


        /// <summary>
        ///  批量插入Person_gen
        /// </summary>
        /// <param name="person_gen">Person_gen实体对象列表</param>
        /// <returns>状态代码</returns>
        public int BulkInsertPeople(IList<Person_gen> person_genList)
        {
            try
            {
                DataTable dt = this.ToDataTable(person_genList,true);

                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter { Value = dt, Name = "TVP_Person", ExtendTypeValue = SqlDbType.Structured, ExtendType = 1 });
                parameters.Add(new StatementParameter { Name = "@return", Direction = ParameterDirection.ReturnValue });

                baseDao.ExecSp("spT_Person_i", parameters);
                return (int)parameters["@return"].Value;
               
            }
            catch (Exception ex)
            {
                throw new DalException("调用Person_genDao时，访问BulkInsert时出错", ex);
            }
        }

        /// <summary>
        ///  批量修改Person_gen
        /// </summary>
        /// <param name="person_gen">Person_gen实体对象列表</param>
        /// <returns>状态代码</returns>
        public int BulkUpdatePeople(IList<Person_gen> person_genList)
        {
            try
            {
                DataTable dt = this.ToDataTable(person_genList,false);

                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter { Value = dt, Name = "TVP_Person", ExtendTypeValue = SqlDbType.Structured, ExtendType = 1 });
                parameters.Add(new StatementParameter { Name = "@return", Direction = ParameterDirection.ReturnValue });

                baseDao.ExecSp("spT_Person_u", parameters);
                return (int)parameters["@return"].Value;
               
            }
            catch (Exception ex)
            {
                throw new DalException("调用Person_genDao时，访问BulkUpdate时出错", ex);
            }
        }

        /// <summary>
        ///  批量删除Person_gen
        /// </summary>
        /// <param name="person_gen">Person_gen实体对象列表</param>
        /// <returns>状态代码</returns>
        public int BulkDeletePeople(IList<Person_gen> person_genList)
        {
            try
            {
                DataTable dt = this.ToDataTable(person_genList,false);

                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter { Value = dt, Name = "TVP_Person", ExtendTypeValue = SqlDbType.Structured, ExtendType = 1 });
                parameters.Add(new StatementParameter { Name = "@return", Direction = ParameterDirection.ReturnValue });

                baseDao.ExecSp("spT_Person_d", parameters);
                return (int)parameters["@return"].Value;
               
            }
            catch (Exception ex)
            {
                throw new DalException("调用Person_genDao时，访问BulkDelete时出错", ex);
            }
        }

		/// <summary>
        ///  GetNameByID
        /// </summary>
        /// <param name="iD"></param>
        /// <returns></returns>
        public IList<Person_gen> GetNameByID(uint iD)
        {
        	try
            {
            	string sql = "SELECT Name FROM Person WHERE  ID = @ID ";
                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter{ Name = "@ID", Direction = ParameterDirection.Input, DbType = DbType.UInt32, Value =iD });

                return baseDao.SelectList<Person_gen>(sql, parameters);

            }
            catch (Exception ex)
            {
                throw new DalException("调用Person_genDao时，访问GetNameByID时出错", ex);
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
                throw new DalException("调用Person_genDao时，访问deleteByName时出错", ex);
            }
        }
        
    }
}