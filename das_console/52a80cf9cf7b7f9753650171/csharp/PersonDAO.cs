using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using platform.dao;
using platform.dao.param;

namespace com.ctrip.flight.intl.engine
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

        public int insertAll(int ID, ${p.getType()} Address, ${p.getType()} Name, ${p.getType()} Telephone, int Age, int Gender, ${p.getType()} Birth) {
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
                                parameters.Add(new ConcreteParameter(){
                        DbType = DbType.${CSharpDbTypeMap.get($p.getType())},
                        Name = "@Address",
                        Direction = ParameterDirection.Input,
                        Index = 0,
                        IsNullable =false,
                        IsSensitive = false,
                        Size  = 50,
                        Value = Address
                    });
                                parameters.Add(new ConcreteParameter(){
                        DbType = DbType.${CSharpDbTypeMap.get($p.getType())},
                        Name = "@Name",
                        Direction = ParameterDirection.Input,
                        Index = 0,
                        IsNullable =false,
                        IsSensitive = false,
                        Size  = 50,
                        Value = Name
                    });
                                parameters.Add(new ConcreteParameter(){
                        DbType = DbType.${CSharpDbTypeMap.get($p.getType())},
                        Name = "@Telephone",
                        Direction = ParameterDirection.Input,
                        Index = 0,
                        IsNullable =false,
                        IsSensitive = false,
                        Size  = 50,
                        Value = Telephone
                    });
                                parameters.Add(new ConcreteParameter(){
                        DbType = DbType.Int32,
                        Name = "@Age",
                        Direction = ParameterDirection.Input,
                        Index = 0,
                        IsNullable =false,
                        IsSensitive = false,
                        Size  = 50,
                        Value = Age
                    });
                                parameters.Add(new ConcreteParameter(){
                        DbType = DbType.Int32,
                        Name = "@Gender",
                        Direction = ParameterDirection.Input,
                        Index = 0,
                        IsNullable =false,
                        IsSensitive = false,
                        Size  = 50,
                        Value = Gender
                    });
                                parameters.Add(new ConcreteParameter(){
                        DbType = DbType.${CSharpDbTypeMap.get($p.getType())},
                        Name = "@Birth",
                        Direction = ParameterDirection.Input,
                        Index = 0,
                        IsNullable =false,
                        IsSensitive = false,
                        Size  = 50,
                        Value = Birth
                    });
                                
                return this.Execute("INSERT INTO Person (ID,Address,Name,Telephone,Age,Gender,Birth) VALUES (@ID,@Address,@Name,@Telephone,@Age,@Gender,@Birth)", parameters.ToArray());
            }
            catch (Exception ex)
            {
                throw ex;
            }
        }
        
        public int setBySpa(int ID, ${p.getType()} Address, ${p.getType()} Name, ${p.getType()} Telephone, int Age, int Gender, ${p.getType()} Birth) {
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
                        DbType = System.Data.DbType.${CSharpDbTypeMap.get($p.getType())},
                        Name = "@Address",
                        Direction = ParameterDirection.Input,
                        Index = 0,
                        IsNullable =false,
                        IsSensitive = false,
                        Size  = 50,
                        Value = Address
                    });
                                parameters.Add(new ConcreteParameter(){
                        DbType = System.Data.DbType.${CSharpDbTypeMap.get($p.getType())},
                        Name = "@Name",
                        Direction = ParameterDirection.Input,
                        Index = 0,
                        IsNullable =false,
                        IsSensitive = false,
                        Size  = 50,
                        Value = Name
                    });
                                parameters.Add(new ConcreteParameter(){
                        DbType = System.Data.DbType.${CSharpDbTypeMap.get($p.getType())},
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
                        DbType = System.Data.DbType.${CSharpDbTypeMap.get($p.getType())},
                        Name = "@Birth",
                        Direction = ParameterDirection.Input,
                        Index = 0,
                        IsNullable =false,
                        IsSensitive = false,
                        Size  = 50,
                        Value = Birth
                    });
                                
                return this.ExecuteSp("spa_Person_u", parameters.ToArray());
            }
            catch (Exception ex)
            {
                throw ex;
            }
        }
        public int deleteBySp3(int ID) {
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
                                
                return this.ExecuteSp("sp3_Person_d", parameters.ToArray());
            }
            catch (Exception ex)
            {
                throw ex;
            }
        }
            }
}
