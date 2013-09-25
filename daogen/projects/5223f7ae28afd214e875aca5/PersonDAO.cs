using System;
using System.Data;
using platform.dao.client;
using platform.dao.param;

namespace platform.international.daogen
{
    public class PersonDAO
    {
        public static IClient client = ClientFactory.CreateDbClient("platform.dao.providers.SqlDatabaseProvider,platform.dao",
            "Server=testdb.dev.sh.ctriptravel.com,28747;Integrated Security=sspi;database=SysDalTest;");

        public static IClient dasClient = ClientFactory.CreateDasClient("SysDalTest", "user=kevin;password=kevin");

        
        // None
        public IDataReader getAllByPk(int iD)
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();

                //parameters.Add(new StatementParameter { Index = 1, DbType = DbType.Int64, Value = pk });
                
                    parameters.Add(new StatementParameter { 
                        Name = "@ID", 
                        Direction = ParameterDirection.Input, 
                        DbType = DbType.Int32, 
                        Value = iD 
                        });
                
                
                string sql = "SELECT ID,Address,Name,Telephone,Age,Gender,Birth FROM Person  WHERE  ID = @ID "   ;

                //return client.Fetch(sql, parameters);
                
                return dasClient.Fetch(sql, parameters);
                
            }
            catch (Exception ex)
            {
                throw ex;
            }
        }
        
        // None
        public int insert(int iD, string address, string name, string telephone, int age, int gender, DateTime birth)
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();

                //parameters.Add(new StatementParameter { Index = 1, DbType = DbType.Int64, Value = pk });
                
                    parameters.Add(new StatementParameter { 
                        Name = "@ID", 
                        Direction = ParameterDirection.Input, 
                        DbType = DbType.Int32, 
                        Value = iD 
                        });
                
                    parameters.Add(new StatementParameter { 
                        Name = "@Address", 
                        Direction = ParameterDirection.Input, 
                        DbType = DbType.String, 
                        Value = address 
                        });
                
                    parameters.Add(new StatementParameter { 
                        Name = "@Name", 
                        Direction = ParameterDirection.Input, 
                        DbType = DbType.String, 
                        Value = name 
                        });
                
                    parameters.Add(new StatementParameter { 
                        Name = "@Telephone", 
                        Direction = ParameterDirection.Input, 
                        DbType = DbType.String, 
                        Value = telephone 
                        });
                
                    parameters.Add(new StatementParameter { 
                        Name = "@Age", 
                        Direction = ParameterDirection.Input, 
                        DbType = DbType.Int32, 
                        Value = age 
                        });
                
                    parameters.Add(new StatementParameter { 
                        Name = "@Gender", 
                        Direction = ParameterDirection.Input, 
                        DbType = DbType.Int32, 
                        Value = gender 
                        });
                
                    parameters.Add(new StatementParameter { 
                        Name = "@Birth", 
                        Direction = ParameterDirection.Input, 
                        DbType = DbType.DateTime, 
                        Value = birth 
                        });
                
                
                string sql = "INSERT INTO Person (ID,Address,Name,Telephone,Age,Gender,Birth) VALUES (@ID,@Address,@Name,@Telephone,@Age,@Gender,@Birth)"   ;

                //return client.Fetch(sql, parameters);
                
                return dasClient.Execute(sql, parameters);
                
            }
            catch (Exception ex)
            {
                throw ex;
            }
        }
        

        
        // None
        public int set(int iD, string address, string name, string telephone, int age, int gender, DateTime birth)
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();

                //parameters.Add(new StatementParameter { Index = 1, DbType = DbType.Int64, Value = pk });
                
                    parameters.Add(new StatementParameter { 
                        Name = "@ID", 
                        Direction = ParameterDirection.Input, 
                        DbType = DbType.Int32, 
                        Value = iD 
                        });
                
                    parameters.Add(new StatementParameter { 
                        Name = "@Address", 
                        Direction = ParameterDirection.Input, 
                        DbType = DbType.String, 
                        Value = address 
                        });
                
                    parameters.Add(new StatementParameter { 
                        Name = "@Name", 
                        Direction = ParameterDirection.Input, 
                        DbType = DbType.String, 
                        Value = name 
                        });
                
                    parameters.Add(new StatementParameter { 
                        Name = "@Telephone", 
                        Direction = ParameterDirection.Input, 
                        DbType = DbType.String, 
                        Value = telephone 
                        });
                
                    parameters.Add(new StatementParameter { 
                        Name = "@Age", 
                        Direction = ParameterDirection.Input, 
                        DbType = DbType.Int32, 
                        Value = age 
                        });
                
                    parameters.Add(new StatementParameter { 
                        Name = "@Gender", 
                        Direction = ParameterDirection.Input, 
                        DbType = DbType.Int32, 
                        Value = gender 
                        });
                
                    parameters.Add(new StatementParameter { 
                        Name = "@Birth", 
                        Direction = ParameterDirection.Input, 
                        DbType = DbType.DateTime, 
                        Value = birth 
                        });
                
                
                string sp = "spa_Person_u"   ;

                //return client.Execute(sql, parameters);

                return dasClient.ExecuteSp(sp, parameters);
            }
            catch (Exception ex)
            {
                throw ex;
            }
        }
        
        // None
        public int delete(int iD)
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();

                //parameters.Add(new StatementParameter { Index = 1, DbType = DbType.Int64, Value = pk });
                
                    parameters.Add(new StatementParameter { 
                        Name = "@ID", 
                        Direction = ParameterDirection.Input, 
                        DbType = DbType.Int32, 
                        Value = iD 
                        });
                
                
                string sp = "spa_Person_d"   ;

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
