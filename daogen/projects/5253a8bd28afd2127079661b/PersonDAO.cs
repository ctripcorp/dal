using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using platform.dao;
using platform.dao.param;

namespace platform.apptools.demo
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

        
        // None
        public IDataReader FetchAllRecords()
        {
            try
            {
                IList<IParameter> parameters = new List<IParameter>();
                
                
                string sql = "SELECT ID,Address,Name,Telephone,Age,Gender,Birth FROM Person "   ;

                //return client.Fetch(sql, parameters);
                
                return this.Fetch(sql, parameters.ToArray());
                
            }
            catch (Exception ex)
            {
                throw ex;
            }
        }
        
        // None
        public IDataReader GetByID(int iD)
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
                        Value = iD
                    });
                
                
                string sql = "SELECT ID,Address,Name,Telephone,Age,Gender,Birth FROM Person  WHERE  ID = @ID "   ;

                //return client.Fetch(sql, parameters);
                
                return this.Fetch(sql, parameters.ToArray());
                
            }
            catch (Exception ex)
            {
                throw ex;
            }
        }
        

        
    }
}
