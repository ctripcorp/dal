using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using platform.dao;
using platform.dao.param;

namespace platform.demo.DAO
{
    public class CommonDAO: AbstractDAO
    {


        public CommonDAO()
        {
            //注释掉此行或者赋值为string.Empty，然后配置connectionString来直连数据库
            PhysicDbName = "HtlProductdb";
            ServicePort = 9000;
            CredentialID = "30303";
            base.Init();
        }

        // None
      

        public IDataReader ExecuteSql(string sql)
        {
            try
            {
                IList<IParameter> parameters = new List<IParameter>();


                return this.Fetch(sql, parameters.ToArray());

            }
            catch (Exception ex)
            {

            }
            return null;
        }


    }
}