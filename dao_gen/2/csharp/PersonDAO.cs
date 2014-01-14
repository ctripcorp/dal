using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using platform.dao;
using platform.dao.param;

namespace com.ctrip.platform.hello
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

        public IDataReader getHello() {
        {
            try
            {
                IList<IParameter> parameters = new List<IParameter>();
                                
                return this.Fetch("SELECT ID,Address,Name,Telephone,Age,Gender,Birth FROM Person WHERE ", parameters.ToArray());
            }
            catch (Exception ex)
            {
                throw ex;
            }
        }
        
            }
}
