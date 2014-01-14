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

        public IDataReader getAllByID(int ID) {
        {
            try
            {
                IList<IParameter> parameters = new List<IParameter>();
                                parameters.Add(new ConcreteParameter(){
                        DbType = DbType.Int32,
                        Name = "@ID",
                        Direction = ParameterDirection.Input,
                        Index = 0,
                        IsNullable =false,
                        IsSensitive = false,
                        Size  = 50,
                        Value = ID
                    });
                                
                return this.Fetch("SELECT ID,Address,Name,Telephone,Age,Gender,Birth FROM Person WHERE  ID = @ID ", parameters.ToArray());
            }
            catch (Exception ex)
            {
                throw ex;
            }
        }
        
        public int insertBySPA(int ID, string Address, string Name, string Telephone, int Age, int Gender, DateTime Birth) {
        {
            try
            {
                IList<IParameter> parameters = new List<IParameter>();
                                parameters.Add(new ConcreteParameter(){
                        DbType = System.Data.DbType.Int32,
                        Name = "@ID",
                        Direction = ParameterDirection.Input,
                        Index = 0,
                        IsNullable =false,
                        IsSensitive = false,
                        Size  = 50,
                        Value = ID
                    });
                                parameters.Add(new ConcreteParameter(){
                        DbType = System.Data.DbType.String,
                        Name = "@Address",
                        Direction = ParameterDirection.Input,
                        Index = 0,
                        IsNullable =false,
                        IsSensitive = false,
                        Size  = 50,
                        Value = Address
                    });
                                parameters.Add(new ConcreteParameter(){
                        DbType = System.Data.DbType.String,
                        Name = "@Name",
                        Direction = ParameterDirection.Input,
                        Index = 0,
                        IsNullable =false,
                        IsSensitive = false,
                        Size  = 50,
                        Value = Name
                    });
                                parameters.Add(new ConcreteParameter(){
                        DbType = System.Data.DbType.String,
                        Name = "@Telephone",
                        Direction = ParameterDirection.Input,
                        Index = 0,
                        IsNullable =false,
                        IsSensitive = false,
                        Size  = 50,
                        Value = Telephone
                    });
                                parameters.Add(new ConcreteParameter(){
                        DbType = System.Data.DbType.Int32,
                        Name = "@Age",
                        Direction = ParameterDirection.Input,
                        Index = 0,
                        IsNullable =false,
                        IsSensitive = false,
                        Size  = 50,
                        Value = Age
                    });
                                parameters.Add(new ConcreteParameter(){
                        DbType = System.Data.DbType.Int32,
                        Name = "@Gender",
                        Direction = ParameterDirection.Input,
                        Index = 0,
                        IsNullable =false,
                        IsSensitive = false,
                        Size  = 50,
                        Value = Gender
                    });
                                parameters.Add(new ConcreteParameter(){
                        DbType = System.Data.DbType.DateTime,
                        Name = "@Birth",
                        Direction = ParameterDirection.Input,
                        Index = 0,
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
