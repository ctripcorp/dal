using System;
using System.Data;
using platform.dao.client;
using platform.dao.param;

namespace platform.international.daogen
{
    public class SysDalTestSPDAO
    {
        public static IClient client = ClientFactory.CreateDbClient("platform.dao.providers.SqlDatabaseProvider,platform.dao",
            "Server=testdb.dev.sh.ctriptravel.com,28747;Integrated Security=sspi;database=SysDalTest;");

        public static IClient dasClient = ClientFactory.CreateDasClient("SysDalTest", "user=kevin;password=kevin");

        
        // None
        public int demoInsertSp(string name, string address)
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();

                //parameters.Add(new StatementParameter { Index = 1, DbType = DbType.Int64, Value = pk });
                
                parameters.Add(new StatementParameter { 
                    Name = "@name", 
                    Direction = ParameterDirection.Input, 
                    DbType = DbType.String, 
                    Value = name 
                    });
                
                parameters.Add(new StatementParameter { 
                    Name = "@address", 
                    Direction = ParameterDirection.Input, 
                    DbType = DbType.String, 
                    Value = address 
                    });
                
                
                string sp = "dbo.demoInsertSp"   ;

                //return client.Execute(sql, parameters);

                
                return dasClient.ExecuteSp(sp, parameters);
                
            }
            catch (Exception ex)
            {
                throw ex;
            }
        }
        
    }
}
