using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using platform.dao;
using platform.dao.param;

namespace com.ctrip.flight.intl.platform
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

        public IDataReader get(int ID, string Address, string Name, string Telephone, int Age) {
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
                        DbType = DbType.String,
                        Name = "@Address",
                        Direction = ParameterDirection.Input,
                        Index = 0,
                        IsNullable =false,
                        IsSensitive = false,
                        Size  = 50,
                        Value = Address
                    });
                                parameters.Add(new ConcreteParameter(){
                        DbType = DbType.String,
                        Name = "@Name",
                        Direction = ParameterDirection.Input,
                        Index = 0,
                        IsNullable =false,
                        IsSensitive = false,
                        Size  = 50,
                        Value = Name
                    });
                                parameters.Add(new ConcreteParameter(){
                        DbType = DbType.String,
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
                                
                return this.Fetch("SELECT ID,Address,Name,Telephone,Age,Gender,Birth FROM Person      WHERE  ID = @ID AND Address = @Address AND Name Like @Name AND Telephone Like @Telephone AND Age <= @Age ", parameters.ToArray());
            }
            catch (Exception ex)
            {
                throw ex;
            }
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
                                
                return this.Fetch("SELECT ID,Address,Name,Telephone,Age,Gender,Birth FROM Person  WHERE  ID = @ID ", parameters.ToArray());
            }
            catch (Exception ex)
            {
                throw ex;
            }
        }
        
            }
}
