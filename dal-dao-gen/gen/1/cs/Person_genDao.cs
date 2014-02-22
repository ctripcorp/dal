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
        /// <returns>状态代码</returns>
        public int InsertPerson_gen(Person_gen person_gen)
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter{ Name = "@ID", Direction = ParameterDirection.InputOutput, DbType = DbType.Int32, Value = person_gen.Id});
                parameters.Add(new StatementParameter{ Name = "@Name", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = person_gen.Name});
                parameters.Add(new StatementParameter{ Name = "@Age", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = person_gen.Age});
                parameters.Add(new StatementParameter{ Name = "@Birth", Direction = ParameterDirection.Input, DbType = DbType.DateTime, Value = person_gen.Birth});
                parameters.Add(new StatementParameter{ Name = "@return",  Direction = ParameterDirection.ReturnValue});

                baseDao.ExecSp("spA_Person_i", parameters);

               person_gen.Id = (int)parameters["@ID"].Value;
                return (int)parameters["@return"].Value;
            }
            catch (Exception ex)
            {
                throw new DalException("调用Person_genDao时，访问Insert时出错", ex);
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
                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter{ Name = "@ID", Direction = ParameterDirection.InputOutput, DbType = DbType.Int32, Value = person_gen.Id});
                parameters.Add(new StatementParameter{ Name = "@Name", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value = person_gen.Name});
                parameters.Add(new StatementParameter{ Name = "@Age", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = person_gen.Age});
                parameters.Add(new StatementParameter{ Name = "@Birth", Direction = ParameterDirection.Input, DbType = DbType.DateTime, Value = person_gen.Birth});
                parameters.Add(new StatementParameter{ Name = "@return",  Direction = ParameterDirection.ReturnValue});

                baseDao.ExecSp("spA_Person_u", parameters);

               person_gen.Id = (int)parameters["@ID"].Value;
                return (int)parameters["@return"].Value;
            }
            catch (Exception ex)
            {
                throw new DalException("调用Person_genDao时，访问Update时出错", ex);
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
                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter{ Name = "@ID", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value = person_gen.Id});
                parameters.Add(new StatementParameter{ Name = "@return",  Direction = ParameterDirection.ReturnValue});

                baseDao.ExecSp("spA_Person_d", parameters);

                return (int)parameters["@return"].Value;
            }
            catch (Exception ex)
            {
                throw new DalException("调用Person_genDao时，访问Delete时出错", ex);
            }
        }
        
        /// <summary>
        /// 删除<#= className #>
        /// </summary>
        /// <param name="iD">@ID #></param>
        /// <returns>状态代码</returns>
        public int DeletePerson_gen(int iD)
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
                throw new DalException("调用Person_genDao时，访问DeletePerson_gen时出错", ex);
            }
        }
        

        /// <summary>
        /// 根据主键获取Person_gen信息
        /// </summary>
        /// <param name="iD"></param>
        /// <returns>Person_gen信息</returns>
        public Person_gen FindByPk(int iD )
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
        ///  转换List为DataTable
        /// </summary>
        /// <param name="person_genlist">Person_gen实体对象列表</param>
        /// <param name="isInsert">Person_gen实体对象列表</param>
        /// <returns>DataTable</returns>
        private DataTable ToDataTable(IList<Person_gen> person_genList , bool insert)
        {
            DataTable dt = new DataTable();
            dt.Columns.Add("Id", typeof(int));
            dt.Columns.Add("Name", typeof(string));
            dt.Columns.Add("Age", typeof(int));
            dt.Columns.Add("Birth", typeof(DateTime));

            int i = 0;
            foreach (Person_gen person_genInfo in person_genList)
            {
                DataRow row = dt.NewRow();
                row["Id"] = insert ? ++i : person_genInfo.Id;
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
        
    }
}
