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
    /// ����DALFx�ӿڹ��ܣ������DALFx Confluence����ַ��
    /// http://conf.ctripcorp.com/display/ARCH/Dal+Fx+API
    /// </summary>
    public partial class Person_genDao : IPerson_genDao
    {
        readonly BaseDao baseDao = BaseDaoFactory.CreateBaseDao("PerformanceTest");
        
        /// <summary>
        ///  ����Person_gen
        /// </summary>
        /// <param name="person_gen">Person_genʵ�����</param>
        /// <returns>����������</returns>
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
                throw new DalException("����Person_genʱ������Insertʱ����", ex);
            }
        }
        
        /// <summary>
        /// �޸�Person_gen
        /// </summary>
        /// <param name="person_gen">Person_genʵ�����</param>
        /// <returns>״̬����</returns>
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
                throw new DalException("����Person_genʱ������Updateʱ����", ex);
            }
        }
        
        /// <summary>
        /// ɾ��Person_gen
        /// </summary>
        /// <param name="person_gen">Person_genʵ�����</param>
        /// <returns>״̬����</returns>
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
                throw new DalException("����Person_genʱ������Deleteʱ����", ex);
            }
        }
        
        

        /// <summary>
        /// ����������ȡPerson_gen��Ϣ
        /// </summary>
        /// <param name="iD"></param>
        /// <returns>Person_gen��Ϣ</returns>
        public Person_gen FindByPk(uint iD )
        {
            try
            {
                return baseDao.GetByKey<Person_gen>(iD);
            }
            catch (Exception ex)
            {
                throw new DalException("����Person_genDaoʱ������FindByPkʱ����", ex);
            }
        }

        /// <summary>
        /// ��ȡ����Person_gen��Ϣ
        /// </summary>
        /// <returns>Person_gen�б�</returns>
        public IList<Person_gen> GetAll()
        {
            try
            {
                return baseDao.GetAll<Person_gen>();
            }
            catch (Exception ex)
            {
                throw new DalException("����Person_genDaoʱ������GetAllʱ����", ex);
            }
        }
        
        /// <summary>
        /// ȡ���ܼ�¼��
        /// </summary>
        /// <returns>��¼��</returns>
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
                throw new DalException("����Person_genDaoʱ������Countʱ����", ex);
            }
        }
        
        /// <summary>
        ///  ����Person_gen������ҳ
        /// </summary>
        /// <param name="obj">Person_genʵ������������</param>
        /// <param name="pagesize">ÿҳ��¼��</param>
        /// <param name="pageNo">ҳ��</param>
        /// <returns>�������</returns>
        public IList<Person_gen> GetListByPage(Person_gen obj, int pagesize, int pageNo)
        {
             try
            {
                var dic = new StatementParameterCollection();
                StringBuilder sbSql = new StringBuilder(200);

                
                 //����ROWNUM
                int fromRownum = (pageNo - 1) * pagesize + 1;
                int endRownum = pagesize * pageNo;
                 sbSql.Append("WITH CTE AS ("); //WITH CTE ��ʼ
                sbSql.Append("select row_number() over(order by ID desc ) as rownum, ");
                sbSql.Append(@"ID, Name, Age, Birth from Person (nolock) ");

                //������ѯ����
                //StringBuilder whereCondition = new StringBuilder();
                //if (!string.IsNullOrEmpty(obj.Name))
                //{
                //    //����
                //    whereCondition.Append("Where Name like @Name ");
                //    dic.AddInParameter("@Name", DbType.String, "%" + obj.Name + "%");
                //}
                //sbSql.Append(whereCondition);

                sbSql.Append(")"); //WITH CTE ����

                // �� CTE ��ɷ�ҳ
                sbSql.Append(@"select ID, Name, Age, Birth from CTE Where rownum between @from and @end");
                dic.AddInParameter("@from", DbType.Int32, fromRownum);
                dic.AddInParameter("@end", DbType.Int32, endRownum);
                IList<Person_gen> list = baseDao.SelectList<Person_gen>(sbSql.ToString(), dic);

                return list;
            }
            catch (Exception ex)
            {
                throw new DalException("����Person_genDaoʱ������GetListByPageʱ����", ex);
            }
        }

        /// <summary>
        ///  ת��ListΪDataTable
        /// </summary>
        /// <param name="person_genlist">Person_genʵ������б�</param>
        /// <param name="isInsert">Person_genʵ������б�</param>
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
        ///  ��������Person_gen
        /// </summary>
        /// <param name="person_gen">Person_genʵ������б�</param>
        /// <returns>״̬����</returns>
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
                throw new DalException("����Person_genDaoʱ������BulkInsertʱ����", ex);
            }
        }

        /// <summary>
        ///  �����޸�Person_gen
        /// </summary>
        /// <param name="person_gen">Person_genʵ������б�</param>
        /// <returns>״̬����</returns>
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
                throw new DalException("����Person_genDaoʱ������BulkUpdateʱ����", ex);
            }
        }

        /// <summary>
        ///  ����ɾ��Person_gen
        /// </summary>
        /// <param name="person_gen">Person_genʵ������б�</param>
        /// <returns>״̬����</returns>
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
                throw new DalException("����Person_genDaoʱ������BulkDeleteʱ����", ex);
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
                throw new DalException("����Person_genDaoʱ������GetNameByIDʱ����", ex);
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
                throw new DalException("����Person_genDaoʱ������deleteByNameʱ����", ex);
            }
        }
        
    }
}