using System;
using System.Data;
using platform.dao.client;
using platform.dao.param;
using platform.demo.Entity;

namespace platform.demo.DAO
{
    public class PersonDAO : AbstractDAO<Person>
    {
        //public static IClient client = ClientFactory.CreateDbClient("platform.dao.providers.SqlDatabaseProvider,platform.dao",
        //    "Server=testdb.dev.sh.ctriptravel.com,28747;Integrated Security=sspi;database=SysDalTest;");

        public static IClient client;

        static PersonDAO()
        {
            client = ClientFactory.CreateDasClient("SysDalTest", "user=kevin;password=kevin");
        }

        public override IDataReader FetchBySql(string sql)
        {
            return client.Fetch(sql, null);
        }

        // None
        public IDataReader getAllByPk(int iD)
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();

                //parameters.Add(new StatementParameter { Index = 1, DbType = DbType.Int64, Value = pk });

                parameters.Add(new StatementParameter
                {
                    Name = "@ID",
                    Direction = ParameterDirection.Input,
                    DbType = DbType.Int32,
                    Value = iD
                });


                string sql = "SELECT ID,Address,Name,Telephone,Age,Gender,Birth FROM Person  WHERE  ID = @ID ";

                //return client.Fetch(sql, parameters);

                return client.Fetch(sql, parameters);

            }
            catch (Exception ex)
            {
                throw ex;
            }
        }

        // None
        public int set(int setId, string setAddress, string setName, string setTelephone, int setAge, int setGender, DateTime setBirth, int whereId)
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();

                //parameters.Add(new StatementParameter { Index = 1, DbType = DbType.Int64, Value = pk });

                parameters.Add(new StatementParameter
                {
                    Name = "@ID",
                    Direction = ParameterDirection.Input,
                    DbType = DbType.Int32,
                    Value = setId
                });

                parameters.Add(new StatementParameter
                {
                    Name = "@Address",
                    Direction = ParameterDirection.Input,
                    DbType = DbType.String,
                    Value = setAddress
                });

                parameters.Add(new StatementParameter
                {
                    Name = "@Name",
                    Direction = ParameterDirection.Input,
                    DbType = DbType.String,
                    Value = setName
                });

                parameters.Add(new StatementParameter
                {
                    Name = "@Telephone",
                    Direction = ParameterDirection.Input,
                    DbType = DbType.String,
                    Value = setTelephone
                });

                parameters.Add(new StatementParameter
                {
                    Name = "@Age",
                    Direction = ParameterDirection.Input,
                    DbType = DbType.Int32,
                    Value = setAge
                });

                parameters.Add(new StatementParameter
                {
                    Name = "@Gender",
                    Direction = ParameterDirection.Input,
                    DbType = DbType.Int32,
                    Value = setGender
                });

                parameters.Add(new StatementParameter
                {
                    Name = "@Birth",
                    Direction = ParameterDirection.Input,
                    DbType = DbType.DateTime,
                    Value = setBirth
                });

                parameters.Add(new StatementParameter
                {
                    Name = "@ID",
                    Direction = ParameterDirection.Input,
                    DbType = DbType.Int32,
                    Value = whereId
                });


                string sql = "UPDATE Person SET ID = @ID,Address = @Address,Name = @Name,Telephone = @Telephone,Age = @Age,Gender = @Gender,Birth = @Birth  WHERE  ID = @ID ";

                //return client.Fetch(sql, parameters);

                return client.Execute(sql, parameters);

            }
            catch (Exception ex)
            {
                throw ex;
            }
        }

        // None
        public IDataReader getAll()
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();

                //parameters.Add(new StatementParameter { Index = 1, DbType = DbType.Int64, Value = pk });


                string sql = "SELECT ID,Address,Name,Telephone,Age,Gender,Birth FROM Person ";

                //return client.Fetch(sql, parameters);

                return client.Fetch(sql, parameters);

            }
            catch (Exception ex)
            {
                throw ex;
            }
        }



        // None
        public int deleteById(int iD)
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();

                //parameters.Add(new StatementParameter { Index = 1, DbType = DbType.Int64, Value = pk });

                parameters.Add(new StatementParameter
                {
                    Name = "@ID",
                    Direction = ParameterDirection.Input,
                    DbType = DbType.Int32,
                    Value = iD
                });


                string sp = "spa_Person_d";

                //return client.Execute(sql, parameters);

                return client.ExecuteSp(sp, parameters);
            }
            catch (Exception ex)
            {
                throw ex;
            }
        }

        // None
        public int insertAll(int iD, string address, string name, string telephone, int age, int gender, DateTime birth)
        {
            try
            {
                StatementParameterCollection parameters = new StatementParameterCollection();

                //parameters.Add(new StatementParameter { Index = 1, DbType = DbType.Int64, Value = pk });

                parameters.Add(new StatementParameter
                {
                    Name = "@ID",
                    Direction = ParameterDirection.Output,
                    DbType = DbType.Int32,
                    Value = iD
                });

                parameters.Add(new StatementParameter
                {
                    Name = "@Address",
                    Direction = ParameterDirection.Input,
                    DbType = DbType.String,
                    Value = address
                });

                parameters.Add(new StatementParameter
                {
                    Name = "@Name",
                    Direction = ParameterDirection.Input,
                    DbType = DbType.String,
                    Value = name
                });

                parameters.Add(new StatementParameter
                {
                    Name = "@Telephone",
                    Direction = ParameterDirection.Input,
                    DbType = DbType.String,
                    Value = telephone
                });

                parameters.Add(new StatementParameter
                {
                    Name = "@Age",
                    Direction = ParameterDirection.Input,
                    DbType = DbType.Int32,
                    Value = age
                });

                parameters.Add(new StatementParameter
                {
                    Name = "@Gender",
                    Direction = ParameterDirection.Input,
                    DbType = DbType.Int32,
                    Value = gender
                });

                parameters.Add(new StatementParameter
                {
                    Name = "@Birth",
                    Direction = ParameterDirection.Input,
                    DbType = DbType.DateTime,
                    Value = birth
                });


                string sp = "spa_Person_i";

                //return client.Execute(sql, parameters);

                return client.ExecuteSp(sp, parameters);
            }
            catch (Exception ex)
            {
                throw ex;
            }
        }

    }
}
