using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using platform.dao.client;

namespace platform.bll
{
    public class PersonDAO : AbstractDAO
    {
        //public static IClient client = ClientFactory.CreateDbClient("platform.dao.providers.SqlDatabaseProvider,platform.dao",
        //   "Server=testdb.dev.sh.ctriptravel.com,28747;Integrated Security=sspi;database=SysDalTest;");

        public static IClient client = ClientFactory.CreateDasClient("SysDalTest", "user=kevin;password=kevin");

        public override System.Data.IDataReader FetchBySql(string sql)
        {
            return client.Fetch(sql);
        }

        public override int ExecuteSql(string sql)
        {
            return client.Execute(sql);
        }

    }
}
