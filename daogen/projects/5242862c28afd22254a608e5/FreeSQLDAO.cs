using System;
using System.Data;
using platform.dao.client;
using platform.dao.param;

namespace platform.international.daogen
{
    public class FreeSQLDAO
    {
        public static IClient client = ClientFactory.CreateDbClient("platform.dao.providers.SqlDatabaseProvider,platform.dao",
            "Server=testdb.dev.sh.ctriptravel.com,28747;Integrated Security=sspi;database=SysDalTest;");

        public static IClient dasClient = ClientFactory.CreateDasClient("SysDalTest", "user=kevin;password=kevin");

        
        // None
        public IDataReader testGet(string Name, string Gender)
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();

                //parameters.Add(new StatementParameter { Index = 1, DbType = DbType.Int64, Value = pk });
                
                    parameters.Add(new StatementParameter { 
                        Name = "@Name", 
                        Direction = ParameterDirection.Input, 
                        DbType = DbType.String, 
                        Value = Name 
                        });
                
                    parameters.Add(new StatementParameter { 
                        Name = "@Gender", 
                        Direction = ParameterDirection.Input, 
                        DbType = DbType.String, 
                        Value = Gender 
                        });
                
                
                string sql = "SELECT Address, Telephone FROM Person WHERE Name = @Name AND Gender IN @Gender"   ;

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
