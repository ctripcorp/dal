using System;
using System.Data;
using platform.dao.client;
using platform.dao.param;

namespace platform.demo.DAO
{
    public class PersonDAO
    {
        //public static IClient client = ClientFactory.CreateDbClient("platform.dao.providers.SqlDatabaseProvider,platform.dao",
        //   "Server=testdb.dev.sh.ctriptravel.com,28747;Integrated Security=sspi;database=SysDalTest;");

        public static IClient client = ClientFactory.CreateDasClient("SysDalTest", "user=kevin;password=kevin");


        // None
        public IDataReader getAllByPk(int iD)
        {
            try
            {



                IParameter iDParam = ParameterFactory.CreateValue(
                        "@ID",
                        iD,
                        direction: ParameterDirection.Input,
                        index: 0,
                        nullable: false,
                        sensitive: false,
                        size: 50
                    );


                string sql = "SELECT ID,Address,Name,Telephone,Age,Gender,Birth FROM Person  WHERE  ID = @ID ";

                //return client.Fetch(sql, parameters);

                return client.Fetch(sql, iDParam);

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



                IParameter setIdParam = ParameterFactory.CreateValue(
                        "@ID",
                        setId,
                        direction: ParameterDirection.Input,
                        index: 0,
                        nullable: false,
                        sensitive: false,
                        size: 50
                    );


                IParameter setAddressParam = ParameterFactory.CreateValue(
                        "@Address",
                        setAddress,
                        direction: ParameterDirection.Input,
                        index: 0,
                        nullable: false,
                        sensitive: false,
                        size: 50
                    );


                IParameter setNameParam = ParameterFactory.CreateValue(
                        "@Name",
                        setName,
                        direction: ParameterDirection.Input,
                        index: 0,
                        nullable: false,
                        sensitive: false,
                        size: 50
                    );


                IParameter setTelephoneParam = ParameterFactory.CreateValue(
                        "@Telephone",
                        setTelephone,
                        direction: ParameterDirection.Input,
                        index: 0,
                        nullable: false,
                        sensitive: false,
                        size: 50
                    );


                IParameter setAgeParam = ParameterFactory.CreateValue(
                        "@Age",
                        setAge,
                        direction: ParameterDirection.Input,
                        index: 0,
                        nullable: false,
                        sensitive: false,
                        size: 50
                    );


                IParameter setGenderParam = ParameterFactory.CreateValue(
                        "@Gender",
                        setGender,
                        direction: ParameterDirection.Input,
                        index: 0,
                        nullable: false,
                        sensitive: false,
                        size: 50
                    );


                IParameter setBirthParam = ParameterFactory.CreateValue(
                        "@Birth",
                        setBirth,
                        direction: ParameterDirection.Input,
                        index: 0,
                        nullable: false,
                        sensitive: false,
                        size: 50
                    );


                IParameter whereIdParam = ParameterFactory.CreateValue(
                        "@ID",
                        whereId,
                        direction: ParameterDirection.Input,
                        index: 0,
                        nullable: false,
                        sensitive: false,
                        size: 50
                    );


                string sql = "UPDATE Person SET ID = @ID,Address = @Address,Name = @Name,Telephone = @Telephone,Age = @Age,Gender = @Gender,Birth = @Birth  WHERE  ID = @ID ";

                //return client.Fetch(sql, parameters);

                return client.Execute(sql, setIdParam, setAddressParam, setNameParam, setTelephoneParam, setAgeParam, setGenderParam, setBirthParam, whereIdParam);

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
                
                
                
                string sql = "SELECT ID,Address,Name,Telephone,Age,Gender,Birth FROM Person "   ;

                //return client.Fetch(sql, parameters);
                
                return client.Fetch(sql);
                
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



                IParameter iDParam = ParameterFactory.CreateValue(
                        "@ID",
                        iD,
                        direction: ParameterDirection.Input,
                        index: 0,
                        nullable: false,
                        sensitive: false,
                        size: 50
                    );


                string sp = "spa_Person_d";

                //return client.Execute(sql, parameters);

                return client.ExecuteSp(sp, iDParam);
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



                IParameter iDParam = ParameterFactory.CreateValue(
                        "@ID",
                        iD,
                        direction: ParameterDirection.Input,
                        index: 0,
                        nullable: false,
                        sensitive: false,
                        size: 50
                    );


                IParameter addressParam = ParameterFactory.CreateValue(
                        "@Address",
                        address,
                        direction: ParameterDirection.Input,
                        index: 0,
                        nullable: false,
                        sensitive: false,
                        size: 50
                    );


                IParameter nameParam = ParameterFactory.CreateValue(
                        "@Name",
                        name,
                        direction: ParameterDirection.Input,
                        index: 0,
                        nullable: false,
                        sensitive: false,
                        size: 50
                    );


                IParameter telephoneParam = ParameterFactory.CreateValue(
                        "@Telephone",
                        telephone,
                        direction: ParameterDirection.Input,
                        index: 0,
                        nullable: false,
                        sensitive: false,
                        size: 50
                    );


                IParameter ageParam = ParameterFactory.CreateValue(
                        "@Age",
                        age,
                        direction: ParameterDirection.Input,
                        index: 0,
                        nullable: false,
                        sensitive: false,
                        size: 50
                    );


                IParameter genderParam = ParameterFactory.CreateValue(
                        "@Gender",
                        gender,
                        direction: ParameterDirection.Input,
                        index: 0,
                        nullable: false,
                        sensitive: false,
                        size: 50
                    );


                IParameter birthParam = ParameterFactory.CreateValue(
                        "@Birth",
                        birth,
                        direction: ParameterDirection.Input,
                        index: 0,
                        nullable: false,
                        sensitive: false,
                        size: 50
                    );


                string sp = "spa_Person_i";

                //return client.Execute(sql, parameters);

                return client.ExecuteSp(sp, iDParam, addressParam, nameParam, telephoneParam, ageParam, genderParam, birthParam);
            }
            catch (Exception ex)
            {
                throw ex;
            }
        }

    }
}
