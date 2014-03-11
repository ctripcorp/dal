using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Text;
using Arch.Data;
using Arch.Data.DbEngine;
using DAL.Entity.DataModel;

namespace DAL.Dao
{
    public partial class JustQueryDao
    {
        readonly BaseDao baseDao = BaseDaoFactory.CreateBaseDao("PerformanceTest");

		/// <summary>
        ///  GetBirthByIDName
        /// </summary>
        /// <param name="iD"></param>
        /// <param name="name"></param>
        /// <returns></returns>
        public IList<> GetBirthByIDName(int iD,string name)
        {
        	try
            {
            	string sql = "select Birth from Person where ID = @ID and Name = @Name";
                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter{ Name = "@ID", Direction = ParameterDirection.Input, DbType = DbType.Int32, Value =iD });
                parameters.Add(new StatementParameter{ Name = "@Name", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value =name });
				//如果只需要一条记录，建议使用limit 1或者top 1，并使用SelectFirst提高性能
				//return baseDao.SelectFirst<>(sql, parameters);
                return baseDao.SelectList<>(sql, parameters);

            }
            catch (Exception ex)
            {
                throw new DalException("调用JustQueryDao时，访问GetBirthByIDName时出错", ex);
            }
        }
		/// <summary>
        ///  hello
        /// </summary>
        /// <returns></returns>
        public IList<JustQuery> hello()
        {
        	try
            {
            	string sql = "select * from person";
                StatementParameterCollection parameters = new StatementParameterCollection();
				//如果只需要一条记录，建议使用limit 1或者top 1，并使用SelectFirst提高性能
				//return baseDao.SelectFirst<JustQuery>(sql, parameters);
                return baseDao.SelectList<JustQuery>(sql, parameters);

            }
            catch (Exception ex)
            {
                throw new DalException("调用JustQueryDao时，访问hello时出错", ex);
            }
        }

    }
}