using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Text;
using Arch.Data;
using Arch.Data.DbEngine;
using com.ctrip.platform.tools.Entity.DataModel;

namespace com.ctrip.platform.tools.Dao
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
        public IList<JustQuery> GetBirthByIDName(uint iD,string name)
        {
        	try
            {
            	string sql = "select Birth from Person where ID = @ID and Name = @Name";
                StatementParameterCollection parameters = new StatementParameterCollection();
                parameters.Add(new StatementParameter{ Name = "@ID", Direction = ParameterDirection.Input, DbType = DbType.UInt32, Value =iD });
                parameters.Add(new StatementParameter{ Name = "@Name", Direction = ParameterDirection.Input, DbType = DbType.AnsiString, Value =name });
				//���ֻ��Ҫһ����¼������ʹ��limit 1����top 1����ʹ��SelectFirst�������
				//return baseDao.SelectFirst<JustQuery>(sql, parameters);
                return baseDao.SelectList<JustQuery>(sql, parameters);

            }
            catch (Exception ex)
            {
                throw new DalException("����JustQueryDaoʱ������GetBirthByIDNameʱ����", ex);
            }
        }

    }
}