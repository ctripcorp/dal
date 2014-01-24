using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using platform.dao;
using platform.dao.param;

namespace com.ctrip.platform.tools
{
    public class SysDalTestSPDAO : AbstractDAO
    {
        public SysDalTestSPDAO()
        {
            //注释掉此行或者赋值为string.Empty，然后配置connectionString来直连数据库
            PhysicDbName = "SysDalTest";
            ServicePort = 9000;
            CredentialID = "30303";
            base.Init();
        }

        public int demoInsertSp(string name, string address) {
        {
            try
            {
                IList<IParameter> parameters = new List<IParameter>();
                                parameters.Add(new ConcreteParameter(){
                        DbType = System.Data.DbType.String,
                        Name = "@name",
                        Direction = ParameterDirection.Input,
                        Index = 1,
                        IsNullable =false,
                        IsSensitive = false,
                        Size  = 50,
                        Value = name
                    });
                                parameters.Add(new ConcreteParameter(){
                        DbType = System.Data.DbType.String,
                        Name = "@address",
                        Direction = ParameterDirection.Input,
                        Index = 2,
                        IsNullable =false,
                        IsSensitive = false,
                        Size  = 50,
                        Value = address
                    });
                                
                return this.ExecuteSP("dbo.demoInsertSp", parameters.ToArray());
            }
            catch (Exception ex)
            {
                throw ex;
            }
        }
            }
}
