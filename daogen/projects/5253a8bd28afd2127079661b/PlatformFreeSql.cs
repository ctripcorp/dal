using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using platform.dao;
using platform.dao.param;

namespace platform.apptools.demo
{
    public class PlatformFreeSql : AbstractDAO
    {
        public PlatformFreeSql()
        {
            //注释掉此行或者赋值为string.Empty，然后配置connectionString来直连数据库
            PhysicDbName = "SysDalTest";
            ServicePort = 9000;
            CredentialID = "30303";
            base.Init();
        }

        
        // None
        public IDataReader GetByName(string Name)
        {
            try
            {
                IList<IParameter> parameters = new List<IParameter>();
 
                
                parameters.Add(new ConcreteParameter() { 
                    DbType = DbType.String,
                    Name = "@Name",
                    Direction = ParameterDirection.Input,
                    Index = 0,
                    IsNullable =false,
                    IsSensitive = false,
                    Size  = 50,
                    Value = Name
                    });
                
                
                string sql = "SELECT Address, Telephone FROM Person WHERE Name = @Name";

                return this.Fetch(sql, parameters);
            }
            catch (Exception ex)
            {
                throw ex;
            }
        }
        
    }
}
