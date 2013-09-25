using System;
using System.Data;
using platform.dao.client;
using platform.dao.param;

namespace platform.bll
{
    public class PersonDAO
    {

        public static IClient client = ClientFactory.CreateDbClient("platform.dao.providers.SqlDatabaseProvider,platform.dao",
            "Server=testdb.dev.sh.ctriptravel.com,28747;Integrated Security=sspi;database=SysDalTest;");

        public static IClient dasClient = ClientFactory.CreateDasClient("SysDalTest", "user=kevin;password=kevin");

        /// <summary>
        /// 根据主键获取地址及名称
        /// </summary>
        /// <param name="pk"></param>
        /// <returns></returns>
        public IDataReader GetAddrNameByPk(int pk)
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();

                //parameters.Add(new StatementParameter { Name = "@ID", Direction = ParameterDirection.Input, DbType = DbType.Int64, Value = pk });

                string sql = "select Address, Name from Person";

                //return client.Fetch(sql, parameters);
                return dasClient.Fetch(sql, parameters);
                
            }
            catch (Exception ex)
            {
                throw ex;
            }
        }



    }
}
