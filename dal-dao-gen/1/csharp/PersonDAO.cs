using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using platform.dao;
using platform.dao.param;

namespace com.ctrip.platform.tools
{
    public class PersonDAO : AbstractDAO
    {
        public PersonDAO()
        {
            //注释掉此行或者赋值为string.Empty，然后配置connectionString来直连数据库
            PhysicDbName = "SysDalTest";
            ServicePort = 9000;
            CredentialID = "30303";
            base.Init();
        }

        public int insertTest1(int ID, string Address, string Name, string Telephone, int Age, int Gender, DateTime Birth) {
        {
            try
            {
                IList<IParameter> parameters = new List<IParameter>();
                                parameters.Add(new ConcreteParameter(){
                        DbType = DbType.Int32,
                        Name = "@ID",
                        Direction = ParameterDirection.Input,
                        Index = 1,
                        IsNullable =false,
                        IsSensitive = false,
                        Size  = 50,
                        Value = ID
                    });
                                parameters.Add(new ConcreteParameter(){
                        DbType = DbType.String,
                        Name = "@Address",
                        Direction = ParameterDirection.Input,
                        Index = 2,
                        IsNullable =false,
                        IsSensitive = false,
                        Size  = 50,
                        Value = Address
                    });
                                parameters.Add(new ConcreteParameter(){
                        DbType = DbType.String,
                        Name = "@Name",
                        Direction = ParameterDirection.Input,
                        Index = 3,
                        IsNullable =false,
                        IsSensitive = false,
                        Size  = 50,
                        Value = Name
                    });
                                parameters.Add(new ConcreteParameter(){
                        DbType = DbType.String,
                        Name = "@Telephone",
                        Direction = ParameterDirection.Input,
                        Index = 4,
                        IsNullable =false,
                        IsSensitive = false,
                        Size  = 50,
                        Value = Telephone
                    });
                                parameters.Add(new ConcreteParameter(){
                        DbType = DbType.Int32,
                        Name = "@Age",
                        Direction = ParameterDirection.Input,
                        Index = 5,
                        IsNullable =false,
                        IsSensitive = false,
                        Size  = 50,
                        Value = Age
                    });
                                parameters.Add(new ConcreteParameter(){
                        DbType = DbType.Int32,
                        Name = "@Gender",
                        Direction = ParameterDirection.Input,
                        Index = 6,
                        IsNullable =false,
                        IsSensitive = false,
                        Size  = 50,
                        Value = Gender
                    });
                                parameters.Add(new ConcreteParameter(){
                        DbType = DbType.DateTime,
                        Name = "@Birth",
                        Direction = ParameterDirection.Input,
                        Index = 7,
                        IsNullable =false,
                        IsSensitive = false,
                        Size  = 50,
                        Value = Birth
                    });
                                
                return this.Execute("INSERT INTO Person (ID,Address,Name,Telephone,Age,Gender,Birth) VALUES ( @ID , @Address , @Name , @Telephone , @Age , @Gender , @Birth )", parameters.ToArray());
            }
            catch (Exception ex)
            {
                throw ex;
            }
        }
        public IDataReader SelectAll1(string Address) {
        {
            try
            {
                IList<IParameter> parameters = new List<IParameter>();
                                parameters.Add(new ConcreteParameter(){
                        DbType = DbType.String,
                        Name = "@Address",
                        Direction = ParameterDirection.Input,
                        Index = 2,
                        IsNullable =false,
                        IsSensitive = false,
                        Size  = 50,
                        Value = Address
                    });
                                
                return this.Fetch("SELECT ID,Address,Name,Telephone,Age,Gender,Birth FROM Person WHERE  Address = @Address ", parameters.ToArray());
            }
            catch (Exception ex)
            {
                throw ex;
            }
        }
        
        public int insertTest333(int ID, string Address, string Name, string Telephone, int Age, int Gender, DateTime Birth) {
        {
            try
            {
                IList<IParameter> parameters = new List<IParameter>();
                parameters.Add(new ConcreteParameter(){
                        DbType = System.Data.DbType.Int32,
                        Name = "@ID",
                        Direction = ParameterDirection.InputOutput,
                        Index = 1,
                        IsNullable =false,
                        IsSensitive = false,
                        Size  = 50,
                        Value = ID
                    });
                parameters.Add(new ConcreteParameter(){
                        DbType = System.Data.DbType.String,
                        Name = "@Address",
                        Direction = ParameterDirection.Input,
                        Index = 2,
                        IsNullable =false,
                        IsSensitive = false,
                        Size  = 50,
                        Value = Address
                    });
                parameters.Add(new ConcreteParameter(){
                        DbType = System.Data.DbType.String,
                        Name = "@Name",
                        Direction = ParameterDirection.Input,
                        Index = 3,
                        IsNullable =false,
                        IsSensitive = false,
                        Size  = 50,
                        Value = Name
                    });
                parameters.Add(new ConcreteParameter(){
                        DbType = System.Data.DbType.String,
                        Name = "@Telephone",
                        Direction = ParameterDirection.Input,
                        Index = 4,
                        IsNullable =false,
                        IsSensitive = false,
                        Size  = 50,
                        Value = Telephone
                    });
                parameters.Add(new ConcreteParameter(){
                        DbType = System.Data.DbType.Int32,
                        Name = "@Age",
                        Direction = ParameterDirection.Input,
                        Index = 5,
                        IsNullable =false,
                        IsSensitive = false,
                        Size  = 50,
                        Value = Age
                    });
                parameters.Add(new ConcreteParameter(){
                        DbType = System.Data.DbType.Int32,
                        Name = "@Gender",
                        Direction = ParameterDirection.Input,
                        Index = 6,
                        IsNullable =false,
                        IsSensitive = false,
                        Size  = 50,
                        Value = Gender
                    });
                parameters.Add(new ConcreteParameter(){
                        DbType = System.Data.DbType.DateTime,
                        Name = "@Birth",
                        Direction = ParameterDirection.Input,
                        Index = 7,
                        IsNullable =false,
                        IsSensitive = false,
                        Size  = 50,
                        Value = Birth
                    });
                                
                return this.ExecuteSp("spa_Person_i", parameters.ToArray());
            }
            catch (Exception ex)
            {
                throw ex;
            }
        }
        }
}
